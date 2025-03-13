package db.models

import slick.driver.MySQLDriver.api._

case class DBConversion(
                         var id: Long,
                         var userId: Long,
                         var fileName: String,
                         var success: Boolean,
                         var conversionTime: Long,
                         var converterHost: String) {

  def equals(obj: DBConversion): Boolean = {
    (id == obj.id) && (userId == obj.userId) && (fileName == obj.fileName) && (success == obj.success) && (conversionTime == obj.conversionTime)
  }
}

class Conversions(tag: Tag) extends Table[DBConversion](tag, "conversions") {
  def id = column[Long]("conversion_id", O.PrimaryKey, O.AutoInc)

  // This is the primary key column
  def userId = column[Long]("user_id")

  def user = foreignKey("user_fk", userId, Tables.users)(_.id, onUpdate = ForeignKeyAction.Restrict, onDelete = ForeignKeyAction.Cascade)

  def fileName = column[String]("file_name")

  def success = column[Boolean]("success")

  def conversionTime = column[Long]("conversion_time")

  def converterHost = column[String]("converter_host")

  // Every table needs a * projection with the same type as the table's type parameter
  def * = (id, userId, fileName, success, conversionTime, converterHost) <>(DBConversion.tupled, DBConversion.unapply)
}