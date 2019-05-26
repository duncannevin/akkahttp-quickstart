package entities

import akka.http.scaladsl.model.{StatusCode, StatusCodes}

final case class ApiSuccess[T] private(statusCode: StatusCode, data: T = "no content")

object ApiSuccess {
  def ok[T](data: T): ApiSuccess[T] = new ApiSuccess[T](StatusCodes.OK, data)
  def noContent[T](data: T): ApiSuccess[T] = new ApiSuccess[T](StatusCodes.NoContent, data)
  def created[T](data: T): ApiSuccess[T] = new ApiSuccess(StatusCodes.Created, data)
}
