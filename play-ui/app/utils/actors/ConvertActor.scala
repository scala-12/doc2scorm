package utils.actors

import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util
import java.util.{Calendar, Comparator, Optional, UUID}
import javax.xml.parsers.{DocumentBuilderFactory, ParserConfigurationException}
import javax.xml.transform._
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

import akka.actor.{Actor, ActorLogging}
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.ipoint.coursegenerator.core.Parser
import com.typesafe.config.ConfigFactory
import org.w3c.dom.{Document, Element, Node, Text}
import utils.actors.ConvertActor._

import scala.reflect.io.{Directory, File}
import scala.util.Try


/**
  * Created by kalas on 13.05.2016.
  */
class ConvertActor extends Actor with ActorLogging {

  override def receive = {
    case ConvertActor.Conversion(courseDocBytes, maxHeader, courseName) =>
      var result: ConvertResultAsCourse = ConvertResultAsCourse(None, None, converterHost)

      if (!localDocs4ConvertDir.exists) {
        localDocs4ConvertDir createDirectory()
      }

      val tmpCourseName = convertRU2ENString(courseName).replaceAll("\\s+", "_").replaceAll("(^_)|(_$)", "") +
        "_" + sdf.format(Calendar.getInstance().getTime) +
        "_" + UUID.randomUUID().toString

      val docFile = File(localDocs4ConvertDir / tmpCourseName + ".docx")
      val docOS = docFile bufferedOutput()
      Stream.continually(docOS write courseDocBytes)
      docOS close()

      log.info(s"Word document was copied for convert (${docFile.name})")

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
        result = ConvertResultAsCourse(
          Some(
            Stream.continually(courseIS read()).
              takeWhile(_ != -1).map(_.toByte).toArray),
          Some(tmpCourseName),
          converterHost)
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

  private val ipointConf = ConfigFactory.load().getObject("ipoint-conf").toConfig

  private val localDocs4ConvertDir = Directory(ipointConf.getString("tmp.doc.dir.local") + "/convert")
  private val actorsDir = Directory(ipointConf getString "tmp.course.dir.actors")
  private val sentCoursesDir = Directory(ipointConf getString "tmp.course.dir.sent")
  private val converterHost = (ipointConf getString "akka-cluster.host") + ':' + (ipointConf getString "akka-cluster.port")

  private val sdf = new SimpleDateFormat("dd-MM-yyyy")
  private val parser: Parser = if (ipointConf.getIsNull("libreoffice.program.soffice")) {
    new Parser()
  } else {
    new Parser(Optional.of(ipointConf.getString("libreoffice.program.soffice")))
  }

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
