package directives

import akka.http.scaladsl.model.{StatusCode, StatusCodes}
import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.testkit.ScalatestRouteTest
import entities.{ApiError, ApiSuccess, ErrorData}
import org.scalatest.{Matchers, WordSpec}

import scala.concurrent.Future

class TodoDirectivesSpec extends WordSpec with Matchers with ScalatestRouteTest with Directives with TodoDirectives {

  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._

  private def mockFailed = {
    Future { Thread.sleep(10); throw new Exception("blah"); 1 }
  }

  private val testRoute = pathPrefix("test") {
    path("success") {
      get {
        handleWithGeneric(Future.unit) (ApiSuccess.ok) { success =>
          complete(success.statusCode, success.data)
        }
      }
    } ~ path("failure") {
      get {
        handleWithGeneric(mockFailed) (ApiSuccess.ok) { success =>
          complete(success.statusCode, success.data)
        }
      }
    }
  }

  "directives.TodoDirectives" should {
    "not return an error if the future succeeds" in {
      Get("/test/success") ~> testRoute ~> check {
        status shouldBe StatusCodes.OK
      }
    }
    "return an error if the future fails" in {
      Get("/test/failure") ~> testRoute ~> check {
        status shouldBe StatusCodes.InternalServerError
        val resp = responseAs[ErrorData]
        resp shouldBe ApiError.generic.data
      }
    }
  }
}
