package directives

import akka.http.scaladsl.model.StatusCodes
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
    pathPrefix("success") {
      path("handle") {
        get {
          handle(Future.unit)(ApiSuccess.ok) { success =>
            complete(success.statusCode, success.data)
          }
        }
      } ~ pathPrefix("handleOption") {
        path("save") {
          get {
            handleOption(Future(Some("success")), "save")(ApiSuccess.ok) { success =>
              complete(success.statusCode, success.data)
            }
          }
        } ~ path("find") {
          handleOption(Future(Some("success")), "find")(ApiSuccess.ok) { success =>
            complete(success.statusCode, success.data)
          }
        }
      }
    } ~ pathPrefix("failure") {
      path("handle") {
        get {
          handle(mockFailed)(ApiSuccess.ok) { _ =>
            complete()
          }
        }
      } ~ pathPrefix("handleOption") {
        path("save") {
          get {
            handleOption(Future(Option.empty[String]), "save")(ApiSuccess.ok) { _ =>
              complete()
            }
          }
        } ~ path("find") {
          handleOption(Future(Option.empty[String]), "find")(ApiSuccess.ok) { _ =>
            complete()
          }
        }
      }
    }
  }

  "directives.TodoDirectives" should {
    "not return an error if the future succeeds [handle]" in {
      Get("/test/success/handle") ~> testRoute ~> check {
        status shouldBe StatusCodes.OK
      }
    }

    "not return an error when return Some value [handleOption][save]" in {
      Get("/test/success/handleOption/save") ~> testRoute ~> check {
        status shouldBe StatusCodes.OK
        val resp = responseAs[String]
        resp shouldBe "success"
      }
    }

    "not return an error when return Some value [handleOption][find]" in {
      Get("/test/success/handleOption/find") ~> testRoute ~> check {
        status shouldBe StatusCodes.OK
        val resp = responseAs[String]
        resp shouldBe "success"
      }
    }

    "return an error if the future fails [handle]" in {
      Get("/test/failure/handle") ~> testRoute ~> check {
        status shouldBe StatusCodes.InternalServerError
        val resp = responseAs[ErrorData]
        resp shouldBe ApiError.generic.data.copy(msg = "blah")
      }
    }

    "return an error when return None value [handleOption][save]" in {
      Get("/test/failure/handleOption/save") ~> testRoute ~> check {
        status shouldBe StatusCodes.Conflict
      }
    }

    "return an error when return None value [handleOption][find]" in {
      Get("/test/failure/handleOption/find") ~> testRoute ~> check {
        status shouldBe StatusCodes.NotFound
      }
    }
  }
}
