package db.models.daos

import scala.concurrent.Future
import com.google.inject.ImplementedBy
import db.models.DBConversion

trait ConversionDao {

  def getSuccessCount(userId: Long): Future[Int]
  
  def addConversion(conversion: DBConversion): Future[Boolean]

}