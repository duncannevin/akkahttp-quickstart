package routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import entities._
import org.scalatest.{BeforeAndAfterEach, Matchers, WordSpec}
import repository.MockRepositories

import scala.concurrent.duration.FiniteDuration
import scala.concurrent.{Await, Future}

class TodoRouterUpdateSpec extends WordSpec with Matchers with ScalatestRouteTest with BeforeAndAfterEach with MockRepositories {
  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._

  val timeout = FiniteDuration(500, "milliseconds")
  var user = User(CreateUser("tester2@chester.com", "tester", "chester"))
  var todo = Todo(1, CreateTodo("update spec", "todo"))

  override def beforeAll(): Unit = {
    Await.result(userRepository.init(), timeout)
    user = Await.result(userRepository.save(user), timeout).get
    Await.result(todoRepository.init(), timeout)
    todo = Await.result(todoRepository.save(todo.copy(userId = user.id.get)), timeout).get
  }

  override def afterAll(): Unit = {
    Await.result(userRepository.drop(), timeout)
    Await.result(todoRepository.drop(), timeout)
  }

  "A TodoRouter" should {
    val router = new TodoRouter(todoRepository)

    "update a users todo" in {
      Put(s"/todos/update?userId=${user.id.get}&id=${todo.id.get}", UpdateTodo(todo.title, todo.description, done = true)) ~> router.route ~> check {
        status shouldBe StatusCodes.OK
        val resp = responseAs[Boolean]
        resp shouldBe true
      }
    }

    "fail if id parameter is missing when calling a route requiring an id" in {
      Put(s"/todos/update?userId=${user.id.get}", UpdateTodo(todo.title, todo.description, done = true)) ~> Route.seal(router.route) ~> check {
        status shouldBe StatusCodes.NotFound
      }
    }
  }
}
