package db.models.daos

import scala.concurrent.Future
import com.google.inject.ImplementedBy
import db.models.DBConversion

trait ConversionDao {

  def successCount(userId: Long): Future[Int]

  def allCount(userId: Long): Future[Int]

  def allUsersConversions(): Future[Map[Long, Map[Boolean, Int]]]

  def addConversion(conversion: DBConversion): Future[Long]

}