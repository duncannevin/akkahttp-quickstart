package routes

import akka.http.scaladsl.server.{Directives, Route}
import directives.{TodoDirectives, ValidatorDirectives}
import entities._
import repository.Repository
import validation.CreateUserValidator

class UserRouter(userRepository: Repository[User]) extends Router with Directives with TodoDirectives with ValidatorDirectives {

  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._

  override def route: Route = pathEndOrSingleSlash {
    // create a new user
    post {
      entity(as[CreateUser]) { createUser =>
        validateWith(CreateUserValidator)(createUser) {
          handleOption(userRepository.save(User(createUser)), `type` = "save")(ApiSuccess.created) { success =>
            complete(success.statusCode, success.data)
          }
        }
      }
    }
  }
}

