package db.models.daos

import play.api.db.slick.DatabaseConfigProvider
import javax.inject.Inject
import slick.driver.JdbcProfile
import db.models.Tables
import slick.driver.MySQLDriver.api._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import db.models.DBUser
import slick.backend.DatabaseConfig

class DBUserDaoImpl @Inject() (dbConfigProvider: DatabaseConfigProvider) extends DBUserDao {

  var dbConfig: DatabaseConfig[JdbcProfile] = null
  var _dbConfigProvider: DatabaseConfigProvider = dbConfigProvider

  def getConf(): DatabaseConfig[JdbcProfile] = {
    if (dbConfig == null) {
      if (_dbConfigProvider != null) {
        dbConfig = _dbConfigProvider.get[JdbcProfile]
      }
    }
    return dbConfig
  }

  def setConf(conf: DatabaseConfig[JdbcProfile]) {
    dbConfig = conf
  }

  def getUserById(id: Long): Future[Option[DBUser]] = {
    val q = Tables.users.filter(_.id === id)
    getConf().db.run(q.result).flatMap { users =>
      {
        if (users.length > 0) {
          Future.successful(Some(users.last))
        } else {
          Future.successful(None)
        }
      }
    }
  }

  def getUserByEmail(email: String): Future[Option[DBUser]] = {
    val q = Tables.users.filter(_.email === email)
    getConf().db.run(q.result).flatMap { users =>
      {
        if (users.length > 0) {
          Future.successful(Some(users.last))
        } else {
          Future.successful(None)
        }
      }
    }
  }

  def getUsers: Future[Seq[DBUser]] = {
    getConf().db.run(Tables.users.result).flatMap { users => Future.successful(users) }
  }

  def updateUserNameAndRole(id: Long, name: String, role: String): Future[Boolean] = {
    val updateAction = Tables.users.filter(_.id === id).map(u => (u.name, u.role)).update((name, role))
    getConf().db.run(updateAction).flatMap { updated =>
      Future.successful(updated > 0)
    }
  }

  def addUser(dbUser: DBUser): Future[Long] = {
    val insert = (Tables.users returning Tables.users.map(_.id)) += dbUser
    getConf().db.run(insert).flatMap { newId => Future.successful(newId) }
  }

}