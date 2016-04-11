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

  val dbUser = new DBUser(0, "cname", "c@email.com", "crole", 555)
  val dbConversion = Seq(new DBConversion(0, 0, "file1", true, 100), new DBConversion(0, 0, "file2", false, 101),
    new DBConversion(0, 0, "file3", true, 102), new DBConversion(0, 0, "file4", true, 103))

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

    "add conversions" in {
      Await.result(dbUserDao.addUser(dbUser).map { newId => dbUser.id = newId }, Duration.Inf)

      var last: DBConversion = null
      dbConversion.foreach { con =>
        con.userId = dbUser.id
        last = con
        Await.result(dbConvDao.addConversion(con).map { newId => last.id = newId }, Duration.Inf)
      }

      var saved: DBConversion = null
      Await.result(dbConfig.db.run(Tables.conversions.result).map { conversions => saved = conversions.last }, Duration.Inf)

      last.equals(saved) mustBe true
    }

  }
}