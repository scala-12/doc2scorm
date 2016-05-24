package controllers

import java.util.Calendar
import javax.inject.Inject

import akka.actor.{ActorSystem, Props}
import akka.pattern
import akka.util.Timeout
import com.mohiva.play.silhouette.api.{Environment, Silhouette}
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import com.mohiva.play.silhouette.impl.providers.SocialProviderRegistry
import com.typesafe.config.ConfigFactory
import db.models.DBConversion
import db.models.daos.ConversionDao
import models.User
import play.api.db.slick.DatabaseConfigProvider
import play.api.i18n.MessagesApi
import play.api.libs.functional.syntax._
import play.api.libs.json._
import play.api.mvc._
import utils.actors.{CourseResult, HostConvertSupervisor}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.reflect.io.{Directory, File}

class ConverterController @Inject()(
                                     val messagesApi: MessagesApi,
                                     val env: Environment[User, CookieAuthenticator],
                                     socialProviderRegistry: SocialProviderRegistry,
                                     dbConfigProvider: DatabaseConfigProvider,
                                     conversionDao: ConversionDao,
                                     configuration: play.api.Configuration)
  extends Silhouette[User, CookieAuthenticator] {

  val clusterConf = ConfigFactory.load().getObject("cluster").toConfig

  private val system = ActorSystem(clusterConf.getString("system-name"),
    clusterConf)
  private val hostSupervisor = system.actorOf(Props(HostConvertSupervisor),
    clusterConf.getString("supervisor-name"))

  private val coursesDir = Directory(configuration.getString("tmp.course.dir.local").get)
  private val sentDir = Directory(configuration.getString("tmp.course.dir.sent").get)

  private val remoteDocsDir = Directory(configuration.getString("tmp.doc.dir.remote").get)
  private val uncorvertedDocs = Directory(configuration.getString("tmp.doc.dir.unconverted").get)

  implicit val rds = (
    (__ \ 'course).read[String] and
      (__ \ 'header).read[String]) tupled

  def upload = Action.async(parse.multipartFormData) { implicit request =>
    request.body.file("doc").map { doc =>
      if (!uncorvertedDocs.exists) {
        uncorvertedDocs.createDirectory()
      }
      val uuid = java.util.UUID.randomUUID().toString
      val target = File(uncorvertedDocs / uuid)
      doc.ref.moveTo(target.jfile)
      Future.successful(Ok("File uploaded").withSession(
        "lastDoc" -> target.path))
    }.getOrElse {
      Future.successful(Results.NotFound("File [doc] not found"))
    }
  }

  def download = SecuredAction.async(parse.json) { implicit request =>
    if (!"GUEST".equals(request.identity.role.get)) {
      request.body.validate[(String, String)].map {
        case (courseName, header) =>
          request.session.get("lastDoc").map { lastDoc => {
            val docFile = File(lastDoc)

            implicit val timeout = Timeout(2.minutes)
            val courseResultFuture = pattern.ask(
              hostSupervisor,
              HostConvertSupervisor.ConversionInCluster(
                docFile toByteArray(),
                header toInt,
                courseName)
            ).asInstanceOf[Future[CourseResult]]

            courseResultFuture flatMap { courseResult =>
              Future successful {
                val localCourse = File(coursesDir / courseResult.fileName.getOrElse("") + ".zip")
                if (courseResult successful) {
                  if (!coursesDir.exists) {
                    coursesDir.createDirectory()
                  }

                  val bos = localCourse bufferedOutput()
                  Stream.continually(bos write courseResult.bytes.get)
                  bos close()

                  val sentCourse = File(sentDir / courseResult.fileName.get + ".zip")
                  val remoteDoc = File(remoteDocsDir / courseResult.fileName.get + ".docx")
                  if (sentCourse exists) {
                    sentCourse delete()
                    docFile delete()
                  } else {
                    if (!remoteDocsDir.exists) {
                      remoteDocsDir createDirectory()
                    }
                    docFile.jfile.renameTo(remoteDoc.jfile)
                  }
                }

                //TODO: what do if error (haven't answer)?
                //TODO: add hostname field to answer and DB
                conversionDao addConversion (new DBConversion(
                  0,
                  request.identity.dbId.get,
                  courseResult.fileName.getOrElse(docFile.name),
                  courseResult.successful,
                  Calendar.getInstance().getTimeInMillis()))
                Ok.sendFile(
                  localCourse.jfile,
                  inline = true).withHeaders(CACHE_CONTROL -> "max-age=3600", CONTENT_DISPOSITION -> "attachment; filename=".concat(localCourse.name), CONTENT_TYPE -> "application/x-download")
              }
            }
          }
          }.getOrElse(Future.successful {
            BadRequest("File was not uploaded")
          })

      }.recoverTotal {
        e => Future.successful {
          BadRequest("Detected error: " + e)
        }
      }
    }

    else {
      Future.successful {
        BadRequest("Permission denied")
      }
    }

  }

}