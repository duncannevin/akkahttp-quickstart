package repository

import db.{Db, TodosTable}
import entities.Todo
import logging.TodoLogger
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

class TodoRepository(val config: DatabaseConfig[JdbcProfile]) extends Repository[Todo] with Db with TodosTable with TodoLogger {
  import config.profile.api._

  /**
    * Once the table mappings (or simplify database schema) are defined,
    * Slick has a capability to project it to a sequence of DDL statements
    * @return
    */
  def init(): Future[Unit] = db.run(DBIO.seq(todos.schema.create))
  def drop(): Future[Unit] = db.run(DBIO.seq(todos.schema.drop))

  init()

  /**
    * Inserting a record is easy. Since we are auto-incrementing the id field
    * we need to get the user in order to get the updated id field
    * @param todo
    * @return
    */
  override def save(todo: Todo): Future[Option[Todo]] = db
    .run((todos returning todos.map(_.id) += todo).asTry)
    .map {
      case Success(id) => Some(todo.copy(id = Some(id)))
      case Failure(e) =>
        failedToSave(e.getMessage, s"todo: ${todo.title}")
        None
    }

  /**
    * Find a todo by id
    * @param id
    * @return
    */
  override def find(id: Int): Future[Option[Todo]] = db.run {
    (for {
      todo <- todos if todo.id === id
    } yield todo).result.headOption
  }

  /**
    * Finds all of a users todos
    * @param userId
    * @return
    */
  override def all(userId: Int): Future[Seq[Todo]] =
    db.run(todos.filter(_.userId === userId).result)

  /**
    * returns all pending todos
    * @param userId
    * @return
    */
  override def pending(userId: Int): Future[Seq[Todo]] =
    db.run(todos.filter(todo => todo.userId === userId && !todo.done).result)

  /**
    * returns all completed todos
    * @param userId
    * @return
    */
  override def done(userId: Int): Future[Seq[Todo]] =
    db.run(todos.filter(todo => todo.userId === userId && todo.done).result)

  /**
    * updates a todos title, description and done
    * @param updateTodo
    * @return
    */
  override def update(updateTodo: Todo): Future[Boolean] = {
    val query = for {
      todo <- todos if todo.id === updateTodo.id
    } yield (todo.description, todo.title, todo.done)

    db.run(query.update((updateTodo.description, updateTodo.title, updateTodo.done)).map(_ > 0))
  }

  /**
    * deletes a user record from the database
    * @param id
    * @return
    */
  override def delete(id: Int): Future[Boolean] =
    db.run(todos.filter(_.id === id).delete).map(_ > 0)
}
