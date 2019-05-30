package db

import logging.TodoLogger
import slick.basic.DatabaseConfig
import slick.dbio.{DBIOAction, NoStream}
import slick.jdbc.JdbcProfile
import slick.lifted.TableQuery

import scala.concurrent.Future

trait Db extends TodoLogger{
  val config: DatabaseConfig[JdbcProfile]
  val db: JdbcProfile#Backend#Database = config.db
}
