package repository

import entities.{CreateTodo, Todo, TodoDeleted, UpdateTodo}

import scala.concurrent.Future

trait TodoMocks {
  class FailingRepository extends Repository {
    override def all(): Future[Seq[Todo]] = Future.failed(new Exception("Mocked exception"))

    override def done(): Future[Seq[Todo]] = Future.failed(new Exception("Mocked exception"))

    override def pending(): Future[Seq[Todo]] = Future.failed(new Exception("Mocked exception"))

    override def save(createTodo: CreateTodo): Future[Todo] = Future.failed(new Exception("Mocked exception"))

    override def update(id: String, updateTodo: UpdateTodo): Future[Todo] = Future.failed(new Exception("Mocked exception"))

    override def delete(id: String): Future[TodoDeleted] = Future.failed(new Exception("Mocked exception"))
  }
}
