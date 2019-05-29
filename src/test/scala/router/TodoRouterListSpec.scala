package router

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import entities.{ApiError, ErrorData, Todo}
import org.scalatest.{Matchers, WordSpec}
import repository.TodoMocks
import routes.TodoRouter

class TodoRouterListSpec extends WordSpec with Matchers with ScalatestRouteTest with TodoMocks {
  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._

  private val pendingTodo = Todo("1", "Buy eggs", "Ran out of eggs, buy a dozen", done = false)
  private val doneTodo = Todo("2", "Buy milk", "The cat is thirsty", done = true)

  private val todos = Seq(doneTodo, pendingTodo)

  "A TodoRouter" should {
    "return all the todos" in {
      val repository = new InMemoryTodoRepository(todos)
      val router = new TodoRouter(repository)

      Get("/todos") ~> router.route ~> check {
        status shouldBe StatusCodes.OK
        val response = responseAs[Seq[Todo]]
        response shouldBe todos
      }
    }
    "return all the done todos" in {
      val repository = new InMemoryTodoRepository(todos)
      val router = new TodoRouter(repository)

      Get("/todos/done") ~> router.route ~> check {
        status shouldBe StatusCodes.OK
        val response = responseAs[Seq[Todo]]
        response shouldBe Seq(doneTodo)
      }
    }
    "return all the pending todos" in {
      val repository = new InMemoryTodoRepository(todos)
      val router = new TodoRouter(repository)

      Get("/todos/pending") ~> router.route ~> check {
        status shouldBe StatusCodes.OK
        val response = responseAs[Seq[Todo]]
        response shouldBe Seq(pendingTodo)
      }
    }
    "handle repository failure in the todos route" in {
      val repository = new FailingRepository
      val router = new TodoRouter(repository)

      Get("/todos") ~> router.route ~> check {
        status shouldBe StatusCodes.InternalServerError
        val resp = responseAs[ErrorData]
        resp shouldBe ApiError.generic.data
      }
    }
    "handle repository failure in the done route" in {
      val repository = new FailingRepository
      val router = new TodoRouter(repository)

      Get("/todos/done") ~> router.route ~> check {
        status shouldBe ApiError.generic.statusCode
        val resp = responseAs[ErrorData]
        resp shouldBe ApiError.generic.data
      }
    }
    "handle repository failure in the pending route" in {
      val repository = new FailingRepository
      val router = new TodoRouter(repository)

      Get("/todos/pending") ~> router.route ~> check {
        status shouldBe ApiError.generic.statusCode
        val resp = responseAs[ErrorData]
        resp shouldBe ApiError.generic.data
      }
    }
  }
}
