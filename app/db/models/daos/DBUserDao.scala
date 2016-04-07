package db.models.daos

import db.models.DBUser
import scala.concurrent.Future
import com.google.inject.ImplementedBy

trait DBUserDao {

  def getUserById(id: Long): Future[DBUser]

  def getUserByEmail(email: String): Future[DBUser]

  def getUsers: Future[Seq[DBUser]]

  def updateUserNameAndRole(id: Long, name: String, role: String): Future[Boolean]

  def addUser(dbUser: DBUser): Future[Long]

}