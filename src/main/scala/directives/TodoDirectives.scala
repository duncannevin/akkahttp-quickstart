package directives

import akka.http.scaladsl.server.{Directive1, Directives}
import entities.{ApiError, ApiSuccess}

import scala.concurrent.Future
import scala.util.{Failure, Success}

trait TodoDirectives extends Directives {
  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._

  def handle[T](f: Future[T])(e: Throwable => ApiError)(success: T => ApiSuccess[T]): Directive1[ApiSuccess[T]] = onComplete(f) flatMap {
    case Success(t) => provide(success(t))
    case Failure(error) =>
      val apiError = e(error)
      complete(apiError.statusCode, apiError.data)
  }

  def handleWithGeneric[T](f: Future[T])(success: T => ApiSuccess[T]): Directive1[ApiSuccess[T]] =
    handle[T](f)(_ => ApiError.generic)(success)
}
