package db.models.daos

import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import scala.concurrent.Future
import db.models.Tables
import slick.driver.MySQLDriver.api._
import scala.concurrent.ExecutionContext.Implicits.global
import db.models.DBConversion
import slick.backend.DatabaseConfig

class ConversionDaoImpl @Inject() (dbConfigProvider: DatabaseConfigProvider) extends ConversionDao {

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

  def getSuccessCount(userId: Long): Future[Int] = {
    val q = Tables.conversions.filter(c => c.userId === userId && c.success).countDistinct
    getConf().db.run(q.result).flatMap { count => Future.successful(count) }
  }

  def addConversion(conversion: DBConversion): Future[Long] = {
    val insert = (Tables.conversions returning Tables.conversions.map(_.id)) += conversion
    getConf().db.run(insert).flatMap { newId => Future.successful(newId) }
  }
}