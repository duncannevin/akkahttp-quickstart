package repository

import scala.concurrent.Future

trait Repository[T] {
  def all(id: String): Future[Seq[T]]
  def done(id: String): Future[Seq[T]]
  def pending(id: String): Future[Seq[T]]
  def save(saveVal: T): Future[T]
  def update(updateVal: T): Future[Boolean]
  def delete(id: String): Future[Boolean]
  def find(id: String): Future[Option[T]]
}
