package db.models.daos

import play.api.db.slick.DatabaseConfigProvider
import javax.inject.Inject
import slick.driver.JdbcProfile
import db.models.Tables
import slick.driver.MySQLDriver.api._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import db.models.DBUser

class DBUserDaoImpl @Inject() (dbConfigProvider: DatabaseConfigProvider) extends DBUserDao {

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  def getUserById(id: Long): Future[DBUser] = {
    val q = Tables.users.filter(_.id === id)
    dbConfig.db.run(q.result).flatMap { users =>
      {
        var dbUser: DBUser = null
        if (users.length > 0) {
          dbUser = users.last
        }
        Future.successful(dbUser)
      }
    }
  }

  def getUserByEmail(email: String): Future[DBUser] = {
    val q = Tables.users.filter(_.email === email)
    dbConfig.db.run(q.result).flatMap { users =>
      {
        var dbUser: DBUser = null
        if (users.length > 0) {
          dbUser = users.last
        }
        Future.successful(dbUser)
      }
    }
  }

  def getUsers: Future[Seq[DBUser]] = {
    dbConfig.db.run(Tables.users.result).flatMap { users => Future.successful(users) }
  }

  def updateUserNameAndRole(id: Long, name: String, role: String): Future[Boolean] = {
    val updateAction = Tables.users.filter(_.id === id).map(u => (u.name, u.role)).update((name, role))
    dbConfig.db.run(updateAction).flatMap { updated =>
      Future.successful(updated > 0)
    }
  }

  def addUser(dbUser: DBUser): Future[Long] = {
    val insert = (Tables.users returning Tables.users.map(_.id)) += dbUser
    dbConfig.db.run(insert).flatMap { newId => Future.successful(newId) }
  }

}