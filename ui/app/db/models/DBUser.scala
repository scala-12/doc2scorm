package db.models

import slick.driver.MySQLDriver.api._
import scala.concurrent.ExecutionContext.Implicits.global

case class DBUser(
    var id: Long,
    var name: String,
    var email: String,
    var role: String,
    var registrationTime: Long) {

  def equals(obj: DBUser): Boolean = {
    return (id == obj.id) && (name == obj.name) && (email == obj.email) && (role == obj.role) && (registrationTime == obj.registrationTime)
  }
}

class Users(tag: Tag) extends Table[DBUser](tag, "users") {
  def id = column[Long]("user_id", O.PrimaryKey, O.AutoInc) // This is the primary key column
  def name = column[String]("name")
  def email = column[String]("email")
  def role = column[String]("role")
  def registrationTime = column[Long]("reg_time")
  // Every table needs a * projection with the same type as the table's type parameter
  def * = (id, name, email, role, registrationTime) <> (DBUser.tupled, DBUser.unapply)
}
