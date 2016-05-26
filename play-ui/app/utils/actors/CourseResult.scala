package utils.actors

import java.io.File

/**
  * Created by kalas on 21.05.2016.
  */
case class CourseResult(
                         bytes: Option[Array[Byte]],
                         fileName: Option[String],
                         successful: Boolean) {

}
