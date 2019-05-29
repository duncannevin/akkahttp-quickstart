package entities

import akka.http.scaladsl.model.{StatusCode, StatusCodes}

final case class ApiError private(statusCode: StatusCode, data: ErrorData)

object ApiError {
  private def apply(statusCode: StatusCode, message: String): ApiError = new ApiError(statusCode, ErrorData(message))

  val generic: ApiError = new ApiError(StatusCodes.InternalServerError, ErrorData("Unknown error."))
  val emptyTitleField: ApiError = new ApiError(StatusCodes.BadRequest, ErrorData("The title field must not be empty."))
  val invalidEmail: ApiError = new ApiError(StatusCodes.BadRequest, ErrorData("Email field is invalid."))
  val notFound: ApiError = new ApiError(StatusCodes.NotFound, ErrorData(s"Not found."))
  val conflict: ApiError = new ApiError(StatusCodes.Conflict, ErrorData(s"Resource already exists."))
}