package db

import entities.Todo

trait TodosTable extends UsersTable { this: Db =>
  import config.profile.api._

  class Todos(tag: Tag) extends Table[Todo](tag, "TODOS") {
    // Columns
    def id = column[Int]("TODO_ID", O.PrimaryKey, O.AutoInc)
    def title = column[String]("TITLE", O.Length(512))
    def description = column[String]("DESCRIPTION", O.Length(1024))
    def done = column[Boolean]("DONE")
    // Foreign Key
    def userId = column[Int]("USER_ID")
    def userFK = foreignKey("USER_FK", userId, users)(_.id)
    // Select
    def * = (id.?, userId, title, description, done) <> (Todo.tupled, Todo.unapply)
  }

  val todos = TableQuery[Todos]
}
