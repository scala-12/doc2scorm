package utils.actors

import java.text.SimpleDateFormat
import java.util.{Calendar, UUID}

import akka.actor.{Actor, ActorLogging}
import com.ipoint.coursegenerator.core.Parser
import com.typesafe.config.ConfigFactory

import scala.reflect.io.{Directory, File}
import scala.util.Try


/**
  * Created by kalas on 13.05.2016.
  */
class ConvertActor extends Actor with ActorLogging {
  val rootConf = ConfigFactory.load()

  def getConfig(name: String) = rootConf.getString(name)

  val parser: Parser = new Parser(rootConf.getString("libreoffice.program.path"))

  val localDocsDir = Directory(rootConf.getString("tmp.doc.dir.local"))
  val actorsDir = Directory(rootConf.getString("tmp.course.dir.actors"))
  val sentCoursesDir = Directory(rootConf.getString("tmp.course.dir.sent"))

  val sdf = new SimpleDateFormat("dd-MM-yyyy")
  val calendar = Calendar.getInstance()

  override def receive = {
    case ConvertActor.Conversion(courseDocBytes, maxHeader, courseName) =>
      var result: CourseResult = new CourseResult(None, None, false)

      if (!localDocsDir.exists) {
        localDocsDir createDirectory()
      }

      val tmpCourseName = courseName +
        "_" + sdf.format(calendar.getTime) +
        "_" + UUID.randomUUID().toString

      val docFile = File(localDocsDir / tmpCourseName + ".docx")
      val docOS = docFile bufferedOutput()
      Stream.continually(docOS write courseDocBytes)
      docOS close()

      log.info(s"Word document was copied (${docFile.name})")

      val courseDir = Directory(actorsDir / tmpCourseName).createDirectory()
      log.info(s"Work in ${courseDir.path})")

      var coursePath: String = null
      val docIS = docFile bufferedInput()
      try {
        coursePath = parser parse(docIS, maxHeader, courseName, courseDir.path)
      } finally {
        docIS close()
      }

      if (coursePath == null) {
        log.error("Word doc was not converted")
        docFile delete()
      } else {
        val courseFile = File(courseDir / tmpCourseName)
        File(courseDir / coursePath).jfile.renameTo(courseFile.jfile)

        log.info(s"Word doc (${docFile.name}) was converted in ${courseFile.path})")

        val courseIS = courseFile bufferedInput()
        result = new CourseResult(
          Some(
            Stream.continually(courseIS read()).
              takeWhile(_ != -1).map(_.toByte).toArray),
          Some(tmpCourseName), true)
        courseIS close()

        if (!sentCoursesDir.exists) {
          sentCoursesDir createDirectory()
        }

        if (courseFile.jfile.renameTo(File(sentCoursesDir / courseFile.name + ".zip").jfile)) {
          log.info("Course was sent to sender")
          Try(courseDir.deleteRecursively())
        }
      }

      sender() ! result
  }

}

object ConvertActor {

  case class Conversion(courseDocBytes: Array[Byte], maxHeader: Int, courseName: String)

}
