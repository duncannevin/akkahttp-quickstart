package repository

import db.{Db, UsersTable}
import entities.User
import logging.TodoLogger
import slick.basic.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

class UserRepository(val config: DatabaseConfig[JdbcProfile]) extends Repository[User] with Db with UsersTable with TodoLogger {
  import config.profile.api._

  /**
    * Once the table mappings (or simplify database schema) are defined,
    * Slick has a capability to project it to a sequence of DDL statements
    * @return
    */
  override def init(): Future[Unit] = {
    db.run(DBIO.seq(users.schema.create).asTry).map {
      case Success(_) => createdTable(users.baseTableRow.tableName)
      case Failure(e) => failedToCreateTable(users.baseTableRow.tableName, e.getMessage)
    }
  }

  override def drop(): Future[Unit] = {
    db.run(DBIO.seq(users.schema.drop).asTry).map {
      case Success(_) => droppedTable(users.baseTableRow.tableName)
      case Failure(e) => failedToDropTable(users.baseTableRow.tableName, e.getMessage)
    }
  }

  /**
    * Inserting a record is easy. Since we are auto-incrementing the id field
    * we need to get the user in order to get the updated id field
    * @param user
    * @return
    */
  override def save(user: User): Future[Option[User]] = db
    .run((users returning users.map(_.id) += user).asTry)
    .map {
      case Success(id) => Some(user.copy(id = Some(id)))
      case Failure(e) =>
        failedToSave(e.getMessage, s"user: ${user.email}")
        None
    }

  /**
    * Find a user by id
    * @param id
    * @return
    */
  override def find(id: Int): Future[Option[User]] = db.run {
    (for {
      user <- users if user.id === id
    } yield user).result.headOption
  }

  /**
    * updates a users firstName, lastName or email
    * @param updateUser
    * @return
    */
  override def update(updateUser: User): Future[Boolean] = {
    val query = for {
      user <- users if user.id === updateUser.id
    } yield (user.firstName, user.lastName, user.email)

    db.run(query.update((updateUser.firstName, updateUser.lastName, updateUser.email)).map(_ > 0))
  }

  /**
    * deletes a user record from the database
    * @param id
    * @return
    */
  override def delete(id: Int): Future[Boolean] =
    db.run(users.filter(_.id === id).delete).map(_ > 0)

  override def all(id: Int): Future[Seq[User]] = Future.failed(throw new Exception("not used"))

  override def done(id: Int): Future[Seq[User]] = Future.failed(throw new Exception("not used"))

  override def pending(id: Int): Future[Seq[User]] = Future.failed(throw new Exception("not used"))
}
