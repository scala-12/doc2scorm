package utils.actors

import java.io.File

/**
  * Created by kalas on 21.05.2016.
  */
case class CourseResult(
                         val bytes: Option[Array[Byte]],
                         val fileName: Option[String],
                         val successful: Boolean) {

}
