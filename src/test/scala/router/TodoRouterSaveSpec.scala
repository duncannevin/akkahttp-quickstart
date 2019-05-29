package router

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import entities.{ApiError, CreateTodo, ErrorData, Todo}
import org.scalatest.{Matchers, WordSpec}
import repository.TodoMocks
import routes.TodoRouter

class TodoRouterSaveSpec extends WordSpec with Matchers with ScalatestRouteTest with TodoMocks {

  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._

  val testSaveTodo = CreateTodo(
    "test todo",
    "test description"
  )

  "A TodoRouter" should {
    "create a todo with valid data" in {
      val repository = new InMemoryTodoRepository()
      val router = new TodoRouter(repository)

      Post("/todos", testSaveTodo) ~> router.route ~> check {
        status shouldBe StatusCodes.Created
        val resp = responseAs[Todo]
        resp.title shouldBe testSaveTodo.title
        resp.description shouldBe testSaveTodo.description
      }
    }
    "not create a todo with invalid data" in {
      val repository = new FailingRepository
      val router = new TodoRouter(repository)

      Post("/todos", testSaveTodo.copy("")) ~> router.route ~> check {
        status shouldBe ApiError.emptyTitleField.statusCode
        val resp = responseAs[ErrorData]
        resp shouldBe ApiError.emptyTitleField.data
      }
    }
  }
}
