package utils

import java.text.SimpleDateFormat

class Tools {

}

object Tools {
  val SIMPLE_DATE_FORMAT = new SimpleDateFormat("dd-MM-yyyy")

  def convertRU2EN(c: Char): String = {
    if (c.isLetter) {
      if (c.isUpper) {
        convertRU2EN(c.toLower).toUpperCase
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
          case 'ъ' => "``"
          case 'ь' => "`"
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

  def convertRU2EN(ruString: String): String = {
    val charArray: Array[Char] = ruString.toCharArray
    charArray.map(c => convertRU2EN(c)).mkString
  }

  def validFileName(name: String): String = {
    name.trim().replaceAll("[^\\w\\d]", "_")
  }

  def validPrettyFileName(name: String): String = {
    val prettyName = validFileName(name).replaceAll("(^_+)|(_+$)", " ").replaceAll("__+", "_")

    if (prettyName.isEmpty) {
      name.hashCode.toString
    } else {
      prettyName
    }
  }
}
