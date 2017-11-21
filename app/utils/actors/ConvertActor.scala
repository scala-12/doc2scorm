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
    case ConvertActor.Preview(courseDocBytes, maxHeader) =>
      if (!localDocs4PreviewDir.exists) {
        localDocs4PreviewDir createDirectory()
      }

      val tmpCourseName = "preview_" + UUID.randomUUID().toString

      val docFile = File(localDocs4PreviewDir / tmpCourseName + ".docx")
      val docOS = docFile bufferedOutput()
      Stream.continually(docOS write courseDocBytes)
      docOS close()

      log.info(s"Word document was copied for preview (${docFile.name})")

      val courseDir = Directory(actorsDir / tmpCourseName).createDirectory()
      log.info(s"Work in ${courseDir.path})")

      var jsonHierarchyInfo: Option[String] = None
      val docIS = docFile bufferedInput()
      try {
        val json = parser getJsonHierarchyInfo(docIS, maxHeader)
        if (json != null) {
          jsonHierarchyInfo = Some(json)
        }
      } finally {
        docIS close()
      }

      val html = if (jsonHierarchyInfo.isDefined) {
        val nodeInfo2Parent = new Gson().fromJson[util.Map[String, String]](jsonHierarchyInfo.get, hierarchyGsonType)
        val nodesInfoMap = new util.HashMap[String, util.Map[String, String]]()
        val nodeInfo2Childs = new util.HashMap[String, util.Set[String]]()

        var iter = nodeInfo2Parent.keySet().iterator()
        while (iter.hasNext) {
          val nodeInfo = iter.next()
          val parentNodeInfo = nodeInfo2Parent.get(nodeInfo)

          val childs = if (nodeInfo2Childs.containsKey(parentNodeInfo))
            new util.HashSet[String](nodeInfo2Childs.get(parentNodeInfo))
          else
            new util.HashSet[String]()
          childs.add(nodeInfo)
          nodeInfo2Childs.put(parentNodeInfo, childs)

          val nodeInfoMap = new Gson().fromJson[util.Map[String, String]](nodeInfo, nodeInfoGsonType)
          nodesInfoMap.put(nodeInfo, nodeInfoMap)
        }

        val nodeInfo2SortedChilds = new util.HashMap[String, util.List[String]]()
        iter = nodeInfo2Childs.keySet().iterator()
        while (iter.hasNext) {
          val nodeInfo = iter.next()

          nodeInfo2SortedChilds.put(nodeInfo, nodeInfo2Childs.get(nodeInfo).stream().sorted(new Comparator[String] {
            override def compare(e1: String, e2: String): Int = nodesInfoMap.get(e1).get("index").toInt - nodesInfoMap.get(e2).get("index").toInt
          }).collect(java.util.stream.Collectors.toList[String]))
        }

        log.info(s"Word doc (${docFile.name}) was converted to preview")

        Some(getNodeHtml(createTreeFromMap("", createEmptyDocument, nodeInfo2SortedChilds, nodesInfoMap, false)))
      } else {
        log.error("Error on preview creating")

        None
      }

      sender() ! ConvertResultAsPreview(html, converterHost)
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
  private val localDocs4PreviewDir = Directory(ipointConf.getString("tmp.doc.dir.local") + "/preview")
  private val actorsDir = Directory(ipointConf getString "tmp.course.dir.actors")
  private val sentCoursesDir = Directory(ipointConf getString "tmp.course.dir.sent")
  private val converterHost = (ipointConf getString "akka-cluster.host") + ':' + (ipointConf getString "akka-cluster.port")

  private val sdf = new SimpleDateFormat("dd-MM-yyyy")

  private val hierarchyGsonType = new TypeToken[util.Map[String, String]]() {}.getType
  private val nodeInfoGsonType = new TypeToken[util.Map[String, String]]() {}.getType

  private val parser: Parser = if (ipointConf.getIsNull("libreoffice.program.soffice")) {
    new Parser()
  } else {
    new Parser(Optional.of(ipointConf.getString("libreoffice.program.soffice")))
  }

  case class Conversion(courseDocBytes: Array[Byte], maxHeader: Int, courseName: String)

  case class Preview(courseDocBytes: Array[Byte], maxHeader: Int)

  private val TRANSFORMER = {
    try {
      val transformer = TransformerFactory.newInstance().newTransformer()
      transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes")

      transformer
    } catch {
      case e@(_: TransformerConfigurationException | _: TransformerFactoryConfigurationError) => null
    }
  }

  private def getNodeHtml(node: Node): String = {
    if (node.isInstanceOf[Text]) {
      node.getTextContent
    } else {
      try {
        val writer = new StringWriter()
        TRANSFORMER.transform(new DOMSource(node), new StreamResult(writer))
        val output = writer.toString

        if (output.indexOf(">") < output.indexOf("?>"))
          output.substring(output.indexOf("?>") + 2)
        else
          output
      } catch {
        case _: TransformerException => null
      }
    }
  }

  private def createEmptyDocument: Document = {
    try {
      DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()
    } catch {
      case _: ParserConfigurationException => null
    }
  }

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

  def createTreeFromMap(parentId: String, doc: Document, id2SortedChilds: util.Map[String, util.List[String]], nodesInfoMap: util.Map[String, util.Map[String, String]], numericalList: Boolean): Element = {
    val list = doc.createElement(if (numericalList) "ol" else "ul")
    val iter = id2SortedChilds.get(parentId).iterator()
    while (iter.hasNext) {
      val nodeInfo = iter.next()

      val item = doc.createElement("li")
      val nodeInfoMap = nodesInfoMap.get(nodeInfo)
      val nodeId = nodeInfoMap.get("id")
      item.setAttribute("id", nodeId)
      item.setTextContent(nodeInfoMap.get("type") + ": " + nodeInfoMap.get("title"))
      if (id2SortedChilds.containsKey(nodeId)) {
        item.appendChild(createTreeFromMap(nodeId, doc, id2SortedChilds, nodesInfoMap, true))
      }
      list.appendChild(item)
    }

    list
  }

}
