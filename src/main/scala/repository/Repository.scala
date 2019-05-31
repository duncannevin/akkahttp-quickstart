package repository

import scala.concurrent.Future

trait Repository[T] {
  def init(): Future[Unit]
  def drop(): Future[Unit]
  def all(id: Int): Future[Seq[T]]
  def done(id: Int): Future[Seq[T]]
  def pending(id: Int): Future[Seq[T]]
  def save(saveVal: T): Future[Option[T]]
  def update(updateVal: T): Future[Boolean]
  def delete(id: Int): Future[Boolean]
  def find(id: Int): Future[Option[T]]
}
