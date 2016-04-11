package controllers

import javax.inject.Inject
import play.api.mvc._
import scala.concurrent.Future
import play.api.data._
import play.api.data.Forms._
import play.api.i18n.MessagesApi
import forms._
import com.mohiva.play.silhouette.api.{ Environment, LogoutEvent, Silhouette }
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import com.mohiva.play.silhouette.impl.providers.SocialProviderRegistry
import models.User
import com.ipoint.coursegenerator.core.Parser
import java.io.File
import java.io.FileInputStream
import scala.util.control.NonFatal
import play.api.libs.json._
import play.api.libs.json.JsObject
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._
import scala.concurrent.ExecutionContext.Implicits.global
import db.models.DBConversion
import java.util.Calendar
import play.Play
import db.models.Tables
import scala.language.postfixOps
import play.api.libs.functional.syntax._
import db.models.daos.ConversionDao

class ConverterController @Inject() (
  val messagesApi: MessagesApi,
  val env: Environment[User, CookieAuthenticator],
  socialProviderRegistry: SocialProviderRegistry,
  dbConfigProvider: DatabaseConfigProvider,
  conversionDao: ConversionDao,
  configuration: play.api.Configuration)
    extends Silhouette[User, CookieAuthenticator] {

  implicit val rds = (
    (__ \ 'course).read[String] and
    (__ \ 'header).read[String]) tupled

  val parser: Parser = new Parser();

  def upload = Action.async(parse.multipartFormData) { implicit request =>
    request.body.file("doc").map { doc =>
      import java.io.File
      val filename = doc.filename
      val contentType = doc.contentType
      var tmpDir = configuration.getString("tmp.dir").get
      val dir = new File(tmpDir)
      dir.mkdirs()
      val uuid = java.util.UUID.randomUUID().toString()
      val target = new File(s"$tmpDir$uuid")
      doc.ref.moveTo(target)
      Future.successful(Ok("File uploaded").withSession(
        "lastDoc" -> target.getAbsolutePath))
    }.getOrElse {
      Future.successful(Results.NotFound("File [doc] not found"))
    }
  }

  def download = SecuredAction.async(parse.json) { implicit request =>
    if (!"GUEST".equals(request.identity.role.get)) {
      request.body.validate[(String, String)].map {
        case (course, header) => {
          request.session.get("lastDoc").map { lastDoc =>
            {
              val doc = new File(lastDoc)
              val fileName = doc.getName
              val headerLevel = header
              val templateDir = configuration.getString("default.template.dir").get
              val courseName = course
              val tempDir = doc.getAbsolutePath().replaceFirst("[.][^.]+$", "") + "_dir";
              val docType = ".docx"

              var success: Boolean = false
              var zipFile: String = ""
              try {
                zipFile = parser.parse(new FileInputStream(doc), headerLevel, templateDir, courseName, tempDir, docType)
                success = true
              } finally {
                conversionDao.addConversion(new DBConversion(0, request.identity.dbId.get, fileName, success, Calendar.getInstance().getTimeInMillis()))
              }

              Future.successful {
                Ok.sendFile(new File(tempDir + "/" + zipFile), inline = true).withHeaders(CACHE_CONTROL -> "max-age=3600", CONTENT_DISPOSITION -> "attachment; filename=".concat(zipFile), CONTENT_TYPE -> "application/x-download")
              }
            }
          }.getOrElse(Future.successful { BadRequest("File was not uploaded") })
        }
      }.recoverTotal {
        e => Future.successful { BadRequest("Detected error: " + e) }
      }
    } else {
      Future.successful { BadRequest("Permission denied") }
    }

  }

}