package controllers

import javax.inject._

import play.api.data.Form
import play.api.data.Forms._
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.io.{Directory, File}

case class FormData(name: String)

@Singleton
class ConverterController @Inject()(cc: MessagesControllerComponents)
                              (implicit executionContext: ExecutionContext)
  extends MessagesAbstractController(cc) {

  private val uploadDocs = Directory("C:\\Users\\vlad\\UserTools\\doc2SCORM\\tmp\\uploaded")


  val form = Form(
    mapping(
      "ms_word" -> text
    )(FormData.apply)(FormData.unapply)
  )

  def index = Action { implicit request =>
    Ok(views.html.index(form))
  }

  def upload = Action.async(parse.multipartFormData) { implicit request =>
    request.body.file("ms_word").map { docFile =>
      if (docFile.contentType.get.toLowerCase().endsWith("document")) {
        if (!uploadDocs.exists) {
          uploadDocs.createDirectory()
        }

        val uploadDoc = File(uploadDocs / (java.util.UUID.randomUUID().toString + ".docx"))
        docFile.ref.moveTo(uploadDoc.jfile)
        Future.successful(
          Ok("File uploaded").withSession(
            "lastDoc" -> uploadDoc.path
          )
        )
      } else {
        Future.successful(
          Results.UnsupportedMediaType("File is not MS Word document")
        )
      }
    }.getOrElse {
      Future.successful(
        Results.NotFound("File [MS Word] not found")
      )
    }
  }
}
