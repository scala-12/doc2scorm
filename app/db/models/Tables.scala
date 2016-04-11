package db.models

import slick.lifted.TableQuery
import slick.driver.MySQLDriver.api._

object Tables {
  val users = TableQuery[Users]
  val conversions = TableQuery[Conversions]

  val setup = DBIO.seq(
    // Create the tables, including primary and foreign keys
    (users.schema ++ conversions.schema).create)

  val clean = DBIO.seq(
    // Create the tables, including primary and foreign keys
    (users.schema ++ conversions.schema).drop)
}

