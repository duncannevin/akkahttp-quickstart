package router

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import entities.{ApiError, ErrorData, Todo, TodoDeleted}
import org.scalatest.{Matchers, WordSpec}
import repository.{InMemoryTodoRepository, TodoMocks}
import routes.TodoRouter

class TodoRouterDeleteSpec extends WordSpec with Matchers with ScalatestRouteTest with TodoMocks {

  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._

  private val pendingTodo = Todo("1", "Buy eggs", "Ran out of eggs, buy a dozen", done = false)
  private val doneTodo = Todo("2", "Buy milk", "The cat is thirsty", done = true)

  private val todos = Seq(doneTodo, pendingTodo)

  "A TodoRouter" should {
    val repository = new InMemoryTodoRepository(todos)
    val router = new TodoRouter(repository)

    "delete a todo if exists" in {
      Delete("/todos/1") ~> router.route ~> check {
        status shouldBe StatusCodes.OK
        val response = responseAs[TodoDeleted]
        response shouldBe TodoDeleted("1")
      }
    }

    "return 404 not found if the record does not exist" in {
      Delete("/todos/1") ~> router.route ~> check {
        status shouldBe StatusCodes.NotFound
      }
    }

    "handle repository failure in the delete route" in {
      val repository = new FailingRepository
      val router = new TodoRouter(repository)

      Delete("/todos/1") ~> router.route ~> check {
        status shouldBe ApiError.generic.statusCode
        val resp = responseAs[ErrorData]
        resp shouldBe ApiError.generic.data
      }
    }
  }
}

