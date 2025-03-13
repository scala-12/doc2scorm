package utils.actors

import java.util.UUID

import scala.util.Random

/**
  * Created by kalas on 17.05.2016.
  */
object NameUtils {
  private val names: Seq[String] = Seq[String](
    "Alexander", "Artem", "Sofia", "Maxim", "Maria", "Anastasia", "Mikhail", "Ivan", "Anna", "Daria", "Daniel"
  )

  private val nameCount = names.size

  def createName: String =
    s"${names(Random.nextInt(nameCount))}_${UUID.randomUUID().toString}"
}
