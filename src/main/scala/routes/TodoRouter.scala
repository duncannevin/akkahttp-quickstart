package routes

import akka.http.scaladsl.server.{Directives, Route}
import directives.{TodoDirectives, ValidatorDirectives}
import entities._
import repository.Repository
import validation.{CreateTodoValidator, UpdateTodoValidator}

class TodoRouter(todoRepository: Repository) extends Router with Directives with TodoDirectives with ValidatorDirectives {

  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._

  override def route: Route = pathPrefix("todos") {
    pathEndOrSingleSlash {
      get {
        handleWithGeneric(todoRepository.all())(ApiSuccess.ok)(success => complete(success.statusCode, success.data))
      } ~ post {
        entity(as[CreateTodo]) { createTodo =>
          validateWith(CreateTodoValidator)(createTodo){
            handleWithGeneric(todoRepository.save(createTodo))(ApiSuccess.created) { success =>
              complete(success.statusCode, success.data)
            }
          }
        }
      }
    } ~ path(Segment) { id: String =>
      put {
        entity(as[UpdateTodo]) { updateTodo =>
          validateWith(UpdateTodoValidator)(updateTodo) {
            handle(todoRepository.update(id, updateTodo)) {
              case TodoNotFound(_) => ApiError.todoNotFound(id)
              case _ => ApiError.generic
            } (ApiSuccess.ok) (success => complete(success.statusCode, success.data))
          }
        }
      }
    } ~ path("done") {
      get {
        handleWithGeneric(todoRepository.done())(ApiSuccess.ok)(success => complete(success.statusCode, success.data))
      }
    } ~ path("pending") {
      get {
        handleWithGeneric(todoRepository.pending())(ApiSuccess.ok)(success => complete(success.statusCode, success.data))
      }
    }
  }
}
