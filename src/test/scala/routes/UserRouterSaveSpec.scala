package routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import db.TestDbConfiguration
import entities.{ApiError, CreateUser, ErrorData, User}
import org.scalatest.{BeforeAndAfterEach, Matchers, WordSpec}
import repository.{MockRepositories, UserRepository}

import scala.concurrent.Await
import scala.concurrent.duration.FiniteDuration

class UserRouterSaveSpec extends WordSpec with Matchers with ScalatestRouteTest with BeforeAndAfterEach with MockRepositories {
  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._

  val timeout = FiniteDuration(500, "milliseconds")
  val user = CreateUser("tester@chester.com", "tester", "chester")

  override def beforeAll: Unit = {
    Await.result(userRepository.init(), timeout)
  }

  override def afterAll: Unit = {
    Await.result(userRepository.drop(), timeout)
  }

  "A UserRouter" should {
    "create a user with valid data" in {
      val router = new UserRouter(userRepository)

      Post("/users", user) ~> router.route ~> check {
        status shouldBe StatusCodes.Created
        val resp = responseAs[User]
        resp.email shouldBe user.email
        resp.firstName shouldBe user.firstName
        resp.lastName shouldBe user.lastName
      }
    }

    "not create a todo with invalid data" in {
      val router = new UserRouter(userRepository)

      Post("/users", user.copy("")) ~> router.route ~> check {
        status shouldBe ApiError.invalidEmail.statusCode
        val resp = responseAs[ErrorData]
        resp shouldBe ApiError.invalidEmail.data
      }
    }
  }
}
