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
import scala.collection.mutable.HashMap

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

  def successCount(userId: Long): Future[Int] = {
    val q = Tables.conversions.filter(c => c.userId === userId && c.success).length
    getConf().db.run(q.result).flatMap { count => Future.successful(count) }
  }

  def allCount(userId: Long): Future[Int] = {
    val q = Tables.conversions.filter(c => c.userId === userId).length
    getConf().db.run(q.result).flatMap {
      count =>
        Future.successful(
          count)
    }
  }

  /**
   * Map of all conversions for every user.
   * Don't contains users without conversions
   */
  def allUsersConversions(): Future[Map[Long, Map[Boolean, Int]]] = {
    val q = Tables.conversions.groupBy(c => (c.userId, c.success)).map {
      case ((userId, success), group) =>
        ((userId, success), group.map(c => (c.userId, c.success)).length)
    }

    getConf().db.run(q.result).flatMap { shortConvsInfo => // if all conversions are success then field "failure" is undefined

      Future successful {
        val fullConvsInfo = HashMap.empty[Long, Map[Boolean, Int]] // if all conversions are success then field "failure" equals 0
        for (convInfo <- shortConvsInfo) {
          if (fullConvsInfo.contains(convInfo._1._1)) {
            val prevFullConvInfo = Map(
              !convInfo._1._2 -> fullConvsInfo.get(convInfo._1._1).get.get(!convInfo._1._2).get,
              convInfo._1._2 -> convInfo._2)
            fullConvsInfo.remove(convInfo._1._1)
            fullConvsInfo += (convInfo._1._1 -> prevFullConvInfo)
          } else {
            fullConvsInfo += (convInfo._1._1 -> Map(!convInfo._1._2 -> 0,
              convInfo._1._2 -> convInfo._2))
          }
        }

        fullConvsInfo.toMap
      }
    }
  }

  def addConversion(conversion: DBConversion): Future[Long] = {
    val insert = (Tables.conversions returning Tables.conversions.map(_.id)) += conversion
    getConf().db.run(insert).flatMap { newId => Future.successful(newId) }
  }
}