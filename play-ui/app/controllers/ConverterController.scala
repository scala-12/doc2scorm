package controllers

import java.nio.file.Files
import java.util.Calendar
import javax.inject.Inject

import akka.actor.{ActorSystem, Props}
import akka.pattern
import akka.util.Timeout
import com.mohiva.play.silhouette.api.{Environment, Silhouette}
import com.mohiva.play.silhouette.impl.authenticators.CookieAuthenticator
import com.mohiva.play.silhouette.impl.providers.SocialProviderRegistry
import db.models.DBConversion
import db.models.daos.ConversionDao
import models.User
import play.api.db.slick.DatabaseConfigProvider
import play.api.i18n.MessagesApi
import play.api.libs.functional.syntax._
import play.api.libs.json.{__, _}
import play.api.mvc._
import utils.actors.{ConvertResultAsCourse, ConvertResultAsPreview, HostConvertSupervisor}

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
  private val ipointConf = configuration.getObject("ipoint-conf").get.toConfig

  private val akkaClusterConf = ipointConf.getObject("akka-cluster.const").toConfig
  private val system = ActorSystem(akkaClusterConf.getString("system-name"),
    akkaClusterConf)
  private val hostSupervisor = system.actorOf(Props(HostConvertSupervisor),
    akkaClusterConf.getString("supervisor-name"))

  private val coursesDir = Directory(ipointConf.getString("tmp.course.dir.local"))
  private val sentDir = Directory(ipointConf.getString("tmp.course.dir.sent"))

  private val remoteDocsDir = Directory(ipointConf.getString("tmp.doc.dir.remote"))
  private val uploadDocs = Directory(ipointConf.getString("tmp.doc.dir.upload"))
  private val unconvertedDocs = Directory(ipointConf.getString("tmp.doc.dir.unconverted"))

  implicit val rds = (
    (__ \ 'course).read[String] and
      (__ \ 'header).read[String]) tupled

  def upload = Action.async(parse.multipartFormData) { implicit request =>
    request.body.file("doc").map { doc =>
      if (!uploadDocs.exists) {
        uploadDocs.createDirectory()
      }
      val uuid = java.util.UUID.randomUUID().toString
      val target = File(uploadDocs / uuid)
      doc.ref.moveTo(target.jfile)
      Future.successful(Ok("File uploaded").withSession(
        "lastDoc" -> target.path))
    }.getOrElse {
      Future.successful(Results.NotFound("File [doc] not found"))
    }
  }

  def preview: Action[JsValue] = SecuredAction.async(parse.json) { implicit request =>
    if (!"GUEST".equals(request.identity.role.get)) {
      val headerValidator = (JsPath \ "header").read[String]
      request.body.validate[String](headerValidator).map {
        case (header) => {
          val headerLevel = header.toInt
          request.session.get("lastDoc").map { lastDoc => {
            val uploadDocFile = File(lastDoc)
            var docFile = File(unconvertedDocs / uploadDocFile.name)
            if (!unconvertedDocs.exists) {
              unconvertedDocs.createDirectory()
            }

            Files.copy(uploadDocFile.jfile.toPath, docFile.outputStream(false))

            implicit val timeout: Timeout = Timeout(2.minutes)
            val courseResultFuture = pattern.ask(
              hostSupervisor,
              HostConvertSupervisor.PreviewInCluster(
                docFile toByteArray(),
                headerLevel)
            ).asInstanceOf[Future[ConvertResultAsPreview]]

            courseResultFuture flatMap { convertResult =>
              Future successful {
                if (convertResult.result.isDefined) {
                  Ok(convertResult.result.get)
                } else {
                  BadRequest("Cannot convert for preview")
                }
              }
            }
          }
          }.getOrElse(Future.successful {
            BadRequest("File was not uploaded")
          })
        }
      }.recoverTotal {
        e =>
          Future.successful {
            BadRequest("Detected error: " + e)
          }
      }
    } else {
      Future.successful {
        BadRequest("Permission denied")
      }
    }
  }

  def download: Action[JsValue] = SecuredAction.async(parse.json) { implicit request =>
    if (!"GUEST".equals(request.identity.role.get)) {
      request.body.validate[(String, String)].map {
        case (courseName, header) =>
          request.session.get("lastDoc").map { lastDoc => {
            val uploadDocFile = File(lastDoc)
            var docFile = File(unconvertedDocs / uploadDocFile.name)
            if (!unconvertedDocs.exists) {
              unconvertedDocs.createDirectory()
            }
            if (!uploadDocFile.jfile.renameTo(docFile.jfile)) {
              docFile = uploadDocFile
            }

            implicit val timeout: Timeout = Timeout(2.minutes)
            val courseResultFuture = pattern.ask(
              hostSupervisor,
              HostConvertSupervisor.ConversionInCluster(
                docFile toByteArray(),
                header toInt,
                courseName)
            ).asInstanceOf[Future[ConvertResultAsCourse]]

            courseResultFuture flatMap { convertResult =>
              Future successful {
                val localCourse = File(coursesDir / convertResult.fileName.getOrElse("") + ".zip")
                if (convertResult.result.isDefined) {
                  if (!coursesDir.exists) {
                    coursesDir.createDirectory()
                  }

                  val bos = localCourse bufferedOutput()
                  Stream.continually(bos write convertResult.result.get)
                  bos close()

                  val sentCourse = File(sentDir / convertResult.fileName.get + ".zip")
                  val remoteDoc = File(remoteDocsDir / convertResult.fileName.get + ".docx")
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
                conversionDao.addConversion(DBConversion(
                  0,
                  request.identity.dbId.get,
                  convertResult.fileName getOrElse docFile.name,
                  convertResult.result.isDefined,
                  Calendar.getInstance() getTimeInMillis(),
                  convertResult converterHost))
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
        e =>
          Future.successful {
            BadRequest("Detected error: " + e)
          }
      }
    } else {
      Future.successful {
        BadRequest("Permission denied")
      }
    }
  }

}