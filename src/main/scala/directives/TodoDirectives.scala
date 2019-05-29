package directives

import akka.http.scaladsl.server.{Directive1, Directives}
import entities.{ApiError, ApiSuccess}

import scala.concurrent.Future
import scala.util.{Failure, Success}

trait TodoDirectives extends Directives {
  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._

  def handle[T](f: Future[T])(success: T => ApiSuccess[T]): Directive1[ApiSuccess[T]] = onComplete(f) flatMap {
    case Success(t) => provide(success(t))
    case Failure(_) =>
      complete(ApiError.generic.statusCode, ApiError.generic.data)
  }

  def handleOption[T](f: Future[Option[T]], `type`: String)(success: T => ApiSuccess[T]): Directive1[ApiSuccess[T]] = onComplete(f) flatMap {
    case Success(tOpt) =>
      tOpt match {
        case Some(t) => provide(success(t))
        case None =>
          if (`type` == "find") {
            complete(ApiError.notFound.statusCode, ApiError.notFound.data)
          } else {
            complete(ApiError.conflict.statusCode, ApiError.conflict.data)
          }
      }
    case Failure(_) =>
      complete(ApiError.generic.statusCode, ApiError.generic.data)
  }
}
