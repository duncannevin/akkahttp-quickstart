package routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import entities._
import org.scalatest.{BeforeAndAfterEach, Matchers, WordSpec}
import repository.MockRepositories

import scala.concurrent.Await
import scala.concurrent.duration.FiniteDuration

class TodoRouterSaveSpec extends WordSpec with Matchers with ScalatestRouteTest with BeforeAndAfterEach with MockRepositories {
  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._

  val timeout = FiniteDuration(500, "milliseconds")
  val todo = CreateTodo("Run Tests", "Until they pass!")
  var user = User(CreateUser("tester2@chester.com", "tester", "chester"))

  override def beforeAll(): Unit = {
    Await.result(userRepository.init(), timeout)
    user = Await.result(userRepository.save(user), timeout).get
    Await.result(todoRepository.init(), timeout)
  }

  override def afterAll(): Unit = {
    Await.result(userRepository.drop(), timeout)
    Await.result(todoRepository.drop(), timeout)
  }

  "A TodoRouter" should {
    "create a todo with valid data" in {
      val router = new TodoRouter(todoRepository)

      Post(s"/todos?userId=${user.id.get}", todo) ~> router.route ~> check {
        status shouldBe StatusCodes.Created
        val resp = responseAs[Todo]
        resp.title shouldBe todo.title
        resp.description shouldBe todo.description
        resp.id shouldBe defined
        resp.userId shouldBe user.id.get
      }
    }

    "not create a todo with invalid data" in {
      val router = new TodoRouter(todoRepository)

      Post(s"/todos?userId=${user.id.get}", todo.copy(title = "")) ~> router.route ~> check {
        status shouldBe ApiError.invalidEmail.statusCode
        val resp = responseAs[ErrorData]
        resp shouldBe ApiError.emptyTitleField.data
      }
    }
  }
}
