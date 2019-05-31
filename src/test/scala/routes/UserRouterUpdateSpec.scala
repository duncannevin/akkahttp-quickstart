package routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.testkit.ScalatestRouteTest
import entities.{ApiError, CreateUser, ErrorData, UpdateUser, User}
import org.scalatest.{BeforeAndAfterEach, Matchers, WordSpec}
import repository.MockRepositories

import scala.concurrent.Await
import scala.concurrent.duration.FiniteDuration

class UserRouterUpdateSpec extends WordSpec with Matchers with ScalatestRouteTest with BeforeAndAfterEach with MockRepositories {
  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._

  val timeout = FiniteDuration(500, "milliseconds")
  var user = User(CreateUser("tester333@chester.com", "tester", "chester"))

  override def beforeAll: Unit = {
    Await.result(userRepository.init(), timeout)
    user = Await.result(userRepository.save(user), timeout).get
  }

  override def afterAll: Unit = {
    Await.result(userRepository.drop(), timeout)
  }

  "A UserRouter" should {
    "update a user with valid data" in {
      val router = new UserRouter(userRepository)
      val updateUser = UpdateUser("dink", "dude", user.email)
      Put(s"/users/update?userId=${user.id.get}", updateUser) ~> router.route ~> check {
        status shouldBe StatusCodes.OK
        val resp = responseAs[Boolean]
        resp shouldBe true
      }
    }

    "not update a todo with invalid data" in {
      val router = new UserRouter(userRepository)
      Put(s"/users/update?userId=${user.id.get}", user.copy(email = "notanemail")) ~> router.route ~> check {
        status shouldBe ApiError.invalidEmail.statusCode
        val resp = responseAs[ErrorData]
        resp shouldBe ApiError.invalidEmail.data
      }
    }
  }
}
