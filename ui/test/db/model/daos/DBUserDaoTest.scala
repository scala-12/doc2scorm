package db.model.daos

import scala.concurrent.ExecutionContext.Implicits.global
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import org.scalatest.FunSuite
import slick.backend.DatabaseConfig
import db.models.daos.DBUserDaoImpl
import org.scalatestplus.play.PlaySpec
import scala.collection.mutable
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import db.models.Tables
import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException
import slick.jdbc.meta._
import org.specs2.specification.BeforeAfterAll
import play.api.test.PlaySpecification
import org.specs2.mutable.Before
import org.specs2.mutable.After
import org.specs2.mutable.BeforeAfter
import play.api.Logger
import db.models.DBUser
import db.models.DBUser

class DBUserDaoTest extends PlaySpec {

  var dbConfig: DatabaseConfig[JdbcProfile] = null
  val dbUserDao = new DBUserDaoImpl(null)

  "DBUserDao" must {
    "empty table" in {
      dbConfig = DatabaseConfig.forConfig[JdbcProfile]("slick.dbs.test")
      dbUserDao.setConf(dbConfig)

      val tables = Await.result(dbConfig.db.run(MTable.getTables), Duration.Inf).toList
      if (tables.length > 0) {
        Await.result(dbConfig.db.run(Tables.clean), Duration.Inf)
      }

      Await.result(dbConfig.db.run(Tables.setup), Duration.Inf)

      var len: Int = 0

      Await.result(dbUserDao.getUsers.map { x => len = x.length }, Duration.Inf)
      len mustBe 0

    }
    "add and get user by id" in {
      var dbUser2: DBUser = null
      Await.result(dbUserDao.getUserById(1).map { user => dbUser2 = user }, Duration.Inf)
      dbUser2 mustBe null
      
      val dbUser: DBUser = new DBUser(0, "testName", "test@email.com", "ROLE", 777)
      var newId: Long = 0
      Await.result(dbUserDao.addUser(dbUser).map { id => newId = id }, Duration.Inf)

      newId mustBe 1

      dbUser.id = newId
      
      Await.result(dbUserDao.getUserById(newId).map { user => dbUser2 = user }, Duration.Inf)
      dbUser2.equals(dbUser) mustBe true
    }

    "get user by email" in {
      val dbUser: DBUser = new DBUser(0, "testName2", "test2@email.com", "ROLE2", 777)
      var newId: Long = 0
      Await.result(dbUserDao.addUser(dbUser).map { id => newId = id }, Duration.Inf)

      dbUser.id = newId
      var dbUser2: DBUser = null
      Await.result(dbUserDao.getUserByEmail(dbUser.email).map { user => dbUser2 = user }, Duration.Inf)
      dbUser2.equals(dbUser) mustBe true
    }

    "update user name and role" in {
      val dbUser: DBUser = new DBUser(0, "testName3", "test3@email.com", "ROLE3", 777)
      var newId: Long = 0
      Await.result(dbUserDao.addUser(dbUser).map { id => newId = id }, Duration.Inf)

      dbUser.id = newId
      var dbUser2: DBUser = null
      Await.result(dbUserDao.getUserById(newId).map { user => dbUser2 = user }, Duration.Inf)
      dbUser2.equals(dbUser) mustBe true

      // rename
      dbUser.name = "new_name"
      dbUser.role = "new_role"

      var updated: Boolean = false
      Await.result(dbUserDao.updateUserNameAndRole(newId, dbUser.name, dbUser.role).map { upd => updated = upd }, Duration.Inf)
      
      updated mustBe true
      Await.result(dbUserDao.getUserById(newId).map { user => dbUser2 = user }, Duration.Inf)
      dbUser2.equals(dbUser) mustBe true
    }

    "get users" in {
      var len: Int = 0
      Await.result(dbUserDao.getUsers.map { users => len = users.length }, Duration.Inf)

      len mustBe 3
    }
  }
}