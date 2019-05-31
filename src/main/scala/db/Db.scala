package db

import logging.TodoLogger
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

trait Db extends TodoLogger{
  val config: DatabaseConfig[JdbcProfile]
  val db: JdbcProfile#Backend#Database = config.db
}
