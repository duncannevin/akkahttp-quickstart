package db

import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

trait DbConfiguration {
  lazy val db: DatabaseConfig[JdbcProfile] = DatabaseConfig.forConfig[JdbcProfile]("mysql")
}
