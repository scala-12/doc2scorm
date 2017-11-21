package utils.actors

/**
  * Created by kalas on 21.05.2016.
  */
abstract class AbstractConvertResult[T] {
  def result: Option[T]

  def converterHost: String
}

case class ConvertResultAsCourse(
                                  result: Option[Array[Byte]],
                                  fileName: Option[String],
                                  converterHost: String
                                ) extends AbstractConvertResult[Array[Byte]] {
}