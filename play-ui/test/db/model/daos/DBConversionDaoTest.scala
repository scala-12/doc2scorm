package db.model.daos

import org.scalatestplus.play.PlaySpec
import slick.backend.DatabaseConfig
import db.models.daos.DBUserDaoImpl
import slick.driver.JdbcProfile
import db.models.Tables
import slick.jdbc.meta.MTable
import scala.concurrent.duration.Duration
import scala.concurrent.Await
import db.models.DBUser
import scala.concurrent.ExecutionContext.Implicits.global
import db.models.daos.ConversionDaoImpl
import slick.driver.MySQLDriver.api._
import db.models.DBConversion
import db.models.DBConversion
import db.models.DBConversion

class DBConversionDaoTest extends PlaySpec {
  var dbConfig: DatabaseConfig[JdbcProfile] = null
  val dbUserDao = new DBUserDaoImpl(null)
  val dbConvDao = new ConversionDaoImpl(null)

  val dbSuccessUser = new DBUser(0, "success man", "s@email.com", "crole", 100)
  val dbFailureUser = new DBUser(1, "failure man", "f@email.com", "crole", 101)
  val dbSimpleUser = new DBUser(2, "simple man", "sf@email.com", "crole", 102)
  val dbEmptyUser = new DBUser(3, "Empty man", "e@email.com", "crole", 103)

  val dbSuccessConversion = Seq(
    new DBConversion(0, 0, "fileS1", true, 200),
    new DBConversion(0, 0, "fileS2", true, 201),
    new DBConversion(0, 0, "fileS3", true, 202),
    new DBConversion(0, 0, "fileS4", true, 203))

  val dbFailureConversion = Seq(
    new DBConversion(0, 0, "fileF1", false, 300),
    new DBConversion(0, 0, "fileF2", false, 301),
    new DBConversion(0, 0, "fileF3", false, 302),
    new DBConversion(0, 0, "fileF4", false, 303))

  val dbSimpleConversion = Seq(
    new DBConversion(0, 0, "fileSF1", false, 400),
    new DBConversion(0, 0, "fileSF2", false, 401),
    new DBConversion(0, 0, "fileSF3", true, 402),
    new DBConversion(0, 0, "fileSF4", true, 403))

  "ConversionDao" must {
    "empty table" in {
      dbConfig = DatabaseConfig.forConfig[JdbcProfile]("slick.dbs.test")
      dbUserDao.setConf(dbConfig)
      dbConvDao.setConf(dbConfig)

      val tables = Await.result(dbConfig.db.run(MTable.getTables), Duration.Inf).toList
      if (tables.length > 0) {
        Await.result(dbConfig.db.run(Tables.clean), Duration.Inf)
      }
      Await.result(dbConfig.db.run(Tables.setup), Duration.Inf)

      var len: Int = 0

      Await.result(dbConfig.db.run(Tables.conversions.result).map { x => len = x.length }, Duration.Inf)
      len mustBe 0
    }

    "add users in table of users" in {
      Await result (dbUserDao addUser (dbSuccessUser)
        map { newId => dbSuccessUser.id = newId }, Duration.Inf)
      Await result (dbUserDao addUser (dbFailureUser)
        map { newId => dbFailureUser.id = newId }, Duration.Inf)
      Await result (dbUserDao addUser (dbSimpleUser)
        map { newId => dbSimpleUser.id = newId }, Duration.Inf)
      Await result (dbUserDao addUser (dbEmptyUser)
        map { newId => dbEmptyUser.id = newId }, Duration.Inf)

      Await.result(dbUserDao.getUsers.map { count => count.length mustBe 4 }, Duration.Inf)
    }

    "add conversions" in {
      var last: DBConversion = null
      dbSuccessConversion.foreach { con =>
        con.userId = dbSuccessUser.id
        last = con
        Await result (dbConvDao addConversion (con)
          map { newId => last.id = newId }, Duration.Inf)
      }

      var saved: DBConversion = null
      Await result (dbConfig.db.run(Tables.conversions.result).
        map { conversions => saved = conversions.last }, Duration.Inf)

      last.equals(saved) mustBe true

      last = null
      dbFailureConversion.foreach { con =>
        con.userId = dbFailureUser.id
        last = con
        Await result (dbConvDao addConversion (con)
          map { newId => last.id = newId }, Duration.Inf)
      }

      saved = null
      Await.result(dbConfig.db.run(Tables.conversions.result).map { conversions => saved = conversions.last }, Duration.Inf)

      last equals (saved) mustBe true

      last = null
      dbSimpleConversion foreach { con =>
        con.userId = dbSimpleUser.id
        last = con
        Await result (dbConvDao addConversion (con)
          map { newId => last.id = newId }, Duration.Inf)
      }

      saved = null
      Await result (dbConfig.db.run(Tables.conversions.result)
        map { conversions => saved = conversions.last }, Duration.Inf)

      last equals (saved) mustBe true
    }

    "count of all conversions for user" in {
      var allCount = 0
      Await result (dbConvDao allCount (dbSuccessUser.id)
        map { count => allCount = count }, Duration.Inf)
      allCount mustBe 4

      allCount = 0
      Await result (dbConvDao allCount (dbFailureUser.id)
        map { count => allCount = count }, Duration.Inf)
      allCount mustBe 4

      allCount = 0
      Await result (dbConvDao allCount (dbSimpleUser.id)
        map { count => allCount = count }, Duration.Inf)
      allCount mustBe 4
    }

    "count of success conversions for user" in {
      var successCount = 0
      Await result (dbConvDao successCount (dbSuccessUser.id)
        map { count => successCount = count }, Duration.Inf)
      successCount mustBe 4

      successCount = 0
      Await result (dbConvDao successCount (dbFailureUser.id)
        map { count => successCount = count }, Duration.Inf)
      successCount mustBe 0

      successCount = 0
      Await result (dbConvDao successCount (dbSimpleUser.id)
        map { count => successCount = count }, Duration.Inf)
      successCount mustBe 2
    }

    "map of users and them conversions (size)" in {
      var usersMap: Map[Long, Map[Boolean, Int]] = null
      Await result (dbConvDao allUsersConversions ()
        map { usersAndCounts => usersMap = usersAndCounts }, Duration.Inf)

      usersMap.size mustBe 3
    }

    "user conversions from map" in {
      var usersMap: Map[Long, Map[Boolean, Int]] = null
      Await result (dbConvDao allUsersConversions ()
        map { usersAndCounts => usersMap = usersAndCounts }, Duration.Inf)

      var conversions = usersMap.get(dbSuccessUser.id).get

      (conversions.get(true).get == 4) mustBe true
      (conversions.get(false).get == 0) mustBe true

      usersMap = null
      Await result (dbConvDao allUsersConversions ()
        map { usersAndCounts => usersMap = usersAndCounts }, Duration.Inf)

      conversions = usersMap.get(dbFailureUser.id).get

      (conversions.get(true).get == 0) mustBe true
      (conversions.get(false).get == 4) mustBe true

      usersMap = null
      Await result (dbConvDao allUsersConversions ()
        map { usersAndCounts => usersMap = usersAndCounts }, Duration.Inf)

      conversions = usersMap.get(dbSimpleUser.id).get

      (conversions.get(true).get == 2) mustBe true
      (conversions.get(false).get == 2) mustBe true

      usersMap = null
      Await result (dbConvDao allUsersConversions ()
        map { usersAndCounts => usersMap = usersAndCounts }, Duration.Inf)

      usersMap contains (dbEmptyUser.id) mustBe false
    }

  }
}