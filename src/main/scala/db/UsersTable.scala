package db

import entities.User

trait UsersTable { this: Db =>
  import config.profile.api._

  class Users(tag: Tag) extends Table[User](tag, "USERS") {
    // Columns
    def id = column[String]("USER_ID", O.PrimaryKey, O.AutoInc)
    def email = column[String]("USER_EMAIL", O.Length(512))
    def firstName = column[String]("FIRST_NAME", O.Length(512))
    def lastName = column[String]("LAST_NAME", O.Length(512))
    // Indexes
    def emailIndex = index("USER_EMAIL_INDEX", email, unique = true)
    // Select
    def * = (id, email, firstName, lastName) <> (User.tupled, User.unapply)
  }

  val users = TableQuery[Users]
}
