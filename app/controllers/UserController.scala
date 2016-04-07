package controllers

import play.api.i18n.MessagesApi
import forms._
import com.mohiva.play.silhouette.api.{ Environment, LogoutEvent, Silhouette }
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import com.mohiva.play.silhouette.impl.providers.SocialProviderRegistry
import models.User
import javax.inject.Inject
import scala.concurrent.Future
import play.api.mvc.Action
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import db.models.Users
import db.models.Tables
import slick.driver.MySQLDriver.api._
import scala.concurrent.ExecutionContext.Implicits.global
import db.models.DBUser
import db.models.DBUser
import java.util.Calendar
import play.api.libs.json._
import play.api.libs.functional.syntax._
import scala.language.postfixOps
import db.models.DBConversion
import db.models.daos.DBUserDao
import db.models.DBUser
import db.models.daos.ConversionDao

class UserController @Inject() (
  val messagesApi: MessagesApi,
  val env: Environment[User, CookieAuthenticator],
  socialProviderRegistry: SocialProviderRegistry,
  dbConfigProvider: DatabaseConfigProvider,
  dbUserDao: DBUserDao,
  conversionDao: ConversionDao)
    extends Silhouette[User, CookieAuthenticator] {

  implicit val rds = (
    (__ \ 'name).read[String] and
    (__ \ 'role).read[String]) tupled

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  def dbUsetToJson(dbUser: DBUser, docs: Int): JsValue = {
    return JsObject(Seq(
      "id" -> JsNumber(dbUser.id),
      "name" -> JsString(dbUser.name),
      "email" -> JsString(dbUser.email),
      "role" -> JsString(dbUser.role),
      "registrationTime" -> JsNumber(dbUser.registrationTime),
      "docs" -> JsNumber(docs)))
  }

  def getCurrentUser = SecuredAction.async { implicit request =>
    // TODO Init database schema
    dbConfig.db.run(Tables.setup)

    dbUserDao.getUserByEmail(request.identity.email.get).flatMap { user =>
      {
        if (user != null) {
          // get docs count
          request.identity.role = Some(user.role)
          request.identity.dbId = Some(user.id)
          conversionDao.getSuccessCount(user.id).flatMap { count => Future.successful { Ok(dbUsetToJson(user, count)) } }
        } else {
          // insert new DBUser
          var role: String = "GUEST";
          var name: String = request.identity.fullName.get
          var email: String = request.identity.email.get

          if (email.endsWith("@ipoint.ru")) {
            role = "ADMIN"
          }
          request.identity.role = Some(role)
          // Add new dbUser
          var dbUser = new DBUser(0, name, email, role, Calendar.getInstance().getTimeInMillis())
          dbUserDao.addUser(dbUser).flatMap { id =>
            Future.successful {
              dbUser.id = id
              request.identity.dbId = Some(id)
              Ok(dbUsetToJson(dbUser, 0))
            }
          }
        }

      }
    }

  }

  def getUsers = SecuredAction.async { implicit request =>
    if ("ADMIN".equals(request.identity.role.get)) {
      dbUserDao.getUsers.flatMap {
        users =>
          Future.successful {
            val json = for (u <- users) yield (dbUsetToJson(u, 0))
            val arr: JsValue = new JsArray(json)
            Ok(arr)
          }
      }
    } else {
      Future.successful { BadRequest("Permission denied") }
    }
  }

  def getUserById(id: Long) = SecuredAction.async { implicit request =>
    if ("ADMIN".equals(request.identity.role.get)) {
      dbUserDao.getUserById(id).flatMap { user =>
        Future.successful {
          if (user != null) {
            Ok(dbUsetToJson(user, 0))
          } else {
            Ok("")
          }
        }
      }
    } else {
      Future.successful { BadRequest("Permission denied") }
    }
  }

  def update(id: Long) = SecuredAction.async(parse.json) { implicit request =>
    if ("ADMIN".equals(request.identity.role.get)) {
      request.body.validate[(String, String)].map {
        case (name, role) => {
          dbUserDao.updateUserNameAndRole(id, name, role).flatMap { isUpdated => Future.successful(Ok("")) }
        }
      }.recoverTotal {
        e => Future.successful { BadRequest("Detected error: " + e) }
      }
    } else {
      Future.successful { BadRequest("Permission denied") }
    }
  }

}