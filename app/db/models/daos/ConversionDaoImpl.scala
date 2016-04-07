package db.models.daos

import javax.inject.Inject
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile
import scala.concurrent.Future
import db.models.Tables
import slick.driver.MySQLDriver.api._
import scala.concurrent.ExecutionContext.Implicits.global
import db.models.DBConversion
class ConversionDaoImpl @Inject() (dbConfigProvider: DatabaseConfigProvider) extends ConversionDao {
  val dbConfig = dbConfigProvider.get[JdbcProfile]

  def getSuccessCount(userId: Long): Future[Int] = {
    val q = Tables.conversions.filter(c => c.userId === userId && c.success).countDistinct
    dbConfig.db.run(q.result).flatMap { count => Future.successful(count) }
  }

  def addConversion(conversion: DBConversion): Future[Boolean] = {
    val q = Tables.conversions += conversion
    dbConfig.db.run(q).flatMap { added => Future.successful(added > 0) }
  }
}