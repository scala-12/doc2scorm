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
import java.util.Calendar
import play.api.libs.json._
import play.api.libs.functional.syntax._
import scala.language.postfixOps
import db.models.DBConversion
import db.models.daos.DBUserDao
import db.models.daos.ConversionDao
import scala.concurrent.duration._
import scala.concurrent.Await

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

  def dbUserToJson(dbUser: DBUser): Future[JsValue] = {
    conversionDao successCount (dbUser.id) flatMap {
      successCount =>
        conversionDao allCount (dbUser.id) flatMap {
          allCount =>
            Future successful {
              dbUserToJson(dbUser, successCount, allCount);
            }
        }
    }
  }

  def dbAllUsersToJson(users: Seq[DBUser]): Future[Seq[JsValue]] = {
    conversionDao allUsersConversions () flatMap {
      usersAndCount =>
        Future successful {
          for (u <- users) yield {
            var failure = 0
            var success = 0
            if (usersAndCount.contains(u.id)) {
              failure = usersAndCount.get(u.id).get.get(false).get
              success = usersAndCount.get(u.id).get.get(true).get
            }

            dbUserToJson(u, success, success + failure)
          }
        }
    }
  }

  private def dbUserToJson(dbUser: DBUser, successCount: Int, allCount: Int): JsValue = {
    JsObject(Seq(
      "id" -> JsNumber(dbUser.id),
      "name" -> JsString(dbUser.name),
      "email" -> JsString(dbUser.email),
      "role" -> JsString(dbUser.role),
      "registrationTime" -> JsNumber(dbUser.registrationTime),
      "successDocs" -> JsNumber(successCount),
      "allDocs" -> JsNumber(allCount)))
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
          dbUserToJson(user).flatMap { json => Future.successful { Ok(json) } }
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
            dbUser.id = id
            request.identity.dbId = Some(id)
            dbUserToJson(user).flatMap { json => Future.successful { Ok(json) } }
          }
        }

      }
    }

  }

  def getUsers = SecuredAction.async { implicit request =>
    if ("ADMIN".equals(request.identity.role.get)) {
      dbUserDao.getUsers.flatMap {
        users =>
          dbAllUsersToJson(users) flatMap { usersJson =>
            Future successful {
              Ok(
                new JsArray(usersJson))
            }
          }
      }
    } else {
      Future successful { BadRequest("Permission denied") }
    }
  }

  def getUserById(id: Long) = SecuredAction.async { implicit request =>
    if ("ADMIN".equals(request.identity.role.get)) {
      dbUserDao.getUserById(id).flatMap { user =>
        if (user != null) {
          dbUserToJson(user).flatMap { json => Future.successful { Ok(json) } }
        } else {
          Future.successful { Ok("") }
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