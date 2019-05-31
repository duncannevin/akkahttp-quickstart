package routes

import akka.http.scaladsl.server.{Directives, Route}
import directives.{TodoDirectives, ValidatorDirectives}
import entities._
import repository.Repository
import validation.{CreateTodoValidator, UpdateTodoValidator}

class TodoRouter(todoRepository: Repository[Todo]) extends Router with Directives with TodoDirectives with ValidatorDirectives {
  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._

  override def route: Route = pathPrefix("todos") {
    parameter('userId.as[Int]) { userId =>
      pathEndOrSingleSlash {
        get {
          handle(todoRepository.all(userId))(ApiSuccess.ok)(success => complete(success.statusCode, success.data))
        } ~ post {
          entity(as[CreateTodo]) { createTodo =>
            validateWith(CreateTodoValidator)(createTodo) {
              handleOption(todoRepository.save(Todo(userId, createTodo)), `type` = "save")(ApiSuccess.created) { success =>
                complete(success.statusCode, success.data)
              }
            }
          }
        }
      } ~ parameter('id.as[Int]) { id =>
        path("query") {
          get {
            handleOption(todoRepository.find(id), `type` = "find")(ApiSuccess.ok)(success => complete(success.statusCode, success.data))
          }
        } ~ path("update") {
          put {
            entity(as[UpdateTodo]) { updateTodo =>
              validateWith(UpdateTodoValidator)(updateTodo) {
                handle(todoRepository.update(Todo(userId, id, updateTodo)))(ApiSuccess.ok)(success => complete(success.statusCode, success.data))
              }
            }
          }
        } ~ path("delete") {
          delete {
            handle(todoRepository.delete(id))(ApiSuccess.ok)(success => complete(success.statusCode, success.data))
          }
        }
      } ~ path("complete") {
        get {
          handle(todoRepository.done(userId))(ApiSuccess.ok)(success => complete(success.statusCode, success.data))
        }
      } ~ path("pending") {
        get {
          handle(todoRepository.pending(userId))(ApiSuccess.ok)(success => complete(success.statusCode, success.data))
        }
      }
    }
  }
}
