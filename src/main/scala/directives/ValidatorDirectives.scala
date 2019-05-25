package directives

import akka.http.scaladsl.server.{Directive0, Directives}
import validation.Validator

trait ValidatorDirectives extends Directives {
  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._
  def validateWith[T](validator: Validator[T])(t: T): Directive0 =
    validator.validate(t) match {
      case Some(apiError) => complete(apiError.statusCode, apiError.data)
      case None => pass
    }
}
