package repository

import entities.{CreateTodo, Todo, UpdateTodo}

import scala.concurrent.Future

trait Repository {
  def all(): Future[Seq[Todo]]
  def done(): Future[Seq[Todo]]
  def pending(): Future[Seq[Todo]]
  def save(createTodo: CreateTodo): Future[Todo]
  def update(id: String, updateTodo: UpdateTodo): Future[Todo]
}
