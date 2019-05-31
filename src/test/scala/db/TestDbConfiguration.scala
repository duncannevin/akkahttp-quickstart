package db

import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

trait TestDbConfiguration {
  lazy val db: DatabaseConfig[JdbcProfile] = DatabaseConfig.forConfig[JdbcProfile]("h2")
}
