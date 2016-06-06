package utils.actors

import java.text.SimpleDateFormat
import java.util.{Calendar, UUID}

import akka.actor.{Actor, ActorLogging}
import com.ipoint.coursegenerator.core.Parser
import com.typesafe.config.ConfigFactory
import utils.actors.ConvertActor.convertRU2ENString

import scala.reflect.io.{Directory, File}
import scala.util.Try


/**
  * Created by kalas on 13.05.2016.
  */
class ConvertActor extends Actor with ActorLogging {
  val rootConf = ConfigFactory.load()

  def getConfig(name: String) = rootConf.getString(name)

  val parser: Parser = new Parser()

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

      val tmpCourseName = convertRU2ENString(courseName).replaceAll("\\s+", "_").replaceAll("(^_)|(_$)", "") +
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

  def convertSingleCharacterRU2EN(c: Char): String = {
    if (c.isLetter) {
      if (c.isUpper) {
        convertSingleCharacterRU2EN(c.toLower).toUpperCase
      } else {
        c match {
          case 'а' => "a"
          case 'б' => "b"
          case 'в' => "v"
          case 'г' => "g"
          case 'д' => "d"
          case 'е' => "e"
          case 'ё' => "e"
          case 'ж' => "zh"
          case 'з' => "z"
          case 'и' => "i"
          case 'й' => "jj"
          case 'к' => "k"
          case 'л' => "l"
          case 'м' => "m"
          case 'н' => "n"
          case 'о' => "o"
          case 'п' => "p"
          case 'р' => "r"
          case 'с' => "s"
          case 'т' => "t"
          case 'у' => "u"
          case 'ф' => "f"
          case 'х' => "h"
          case 'ц' => "c"
          case 'ч' => "ch"
          case 'ш' => "sh"
          case 'щ' => "shh"
          case 'ы' => "y"
          case 'ъ' | 'ь' => ""
          case 'э' => "eh"
          case 'ю' => "ju"
          case 'я' => "ya"
          case _ => String.valueOf(c)
        }
      }
    } else {
      String.valueOf(c)
    }
  }

  def convertRU2ENString(ruString: String): String = {
    val charArray: Array[Char] = ruString.toCharArray
    charArray.map(c => convertSingleCharacterRU2EN(c)).mkString
  }

}
