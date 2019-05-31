package routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest
import entities._
import org.scalatest.{BeforeAndAfterEach, Matchers, WordSpec}
import repository.MockRepositories

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.FiniteDuration

class TodoRouterGetSpec extends WordSpec with Matchers with ScalatestRouteTest with BeforeAndAfterEach with MockRepositories {
  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._

  val timeout = FiniteDuration(500, "milliseconds")
  var user = User(CreateUser("tester2@chester.com", "tester", "chester"))
  val createTodos = Seq(
    CreateTodo("one", "d-one"),
    CreateTodo("two", "d-two"),
    CreateTodo("three", "d-three"),
    CreateTodo("four", "d-four"),
    CreateTodo("five", "d-five"),
    CreateTodo("six", "d-six")
  )
  var todos = Seq.empty[Todo]

  override def beforeAll(): Unit = {
    Await.result(userRepository.init(), timeout)
    user = Await.result(userRepository.save(user), timeout).get
    Await.result(todoRepository.init(), timeout)
    Await.result(
      Future.sequence {
        createTodos.map{ ct =>
          todoRepository.save(Todo(user.id.get, ct)).map {
            case Some(todo) =>
              todos = todos :+ todo
              Some(todo)
            case None => None
          }
        }
      }
      , timeout
    )
    Await.result(todoRepository.update(todos(3).copy(done = true)), timeout)
  }

  override def afterAll(): Unit = {
    Await.result(userRepository.drop(), timeout)
    Await.result(todoRepository.drop(), timeout)
  }

  "A TodoRouter" should {
    val router = new TodoRouter(todoRepository)

    "get all a users todos" in {
      Get(s"/todos?userId=${user.id.get}") ~> router.route ~> check {
        status shouldBe StatusCodes.OK
        val resp = responseAs[Seq[Todo]]
        resp.exists(_.id == todos.head.id) shouldBe true
        resp.exists(_.id == todos(1).id) shouldBe true
        resp.exists(_.id == todos(2).id) shouldBe true
        resp.exists(_.id == todos(3).id) shouldBe true
      }
    }

    "get all of a users pending todos" in {
      val updatedTodo = todos(3)
      Get(s"/todos/pending?userId=${user.id.get}") ~> router.route ~> check {
        status shouldBe StatusCodes.OK
        val resp = responseAs[Seq[Todo]]
        resp.exists(_.id == updatedTodo.id) shouldBe false
        resp.exists(_.id == todos(1).id) shouldBe true
      }
    }

    "get all of a users completed todos" in {
      val updatedTodo = todos(3)
      Get(s"/todos/complete?userId=${user.id.get}") ~> router.route ~> check {
        status shouldBe StatusCodes.OK
        val resp = responseAs[Seq[Todo]]
        resp.exists(_.id == updatedTodo.id) shouldBe true
        resp.exists(_.id == todos(1).id) shouldBe false
      }
    }

    "query a users specific todo" in {
      Get(s"/todos/query?userId=${user.id.get}&id=${todos.head.id.get}") ~> router.route ~> check {
        status shouldBe StatusCodes.OK
        val resp = responseAs[Todo]
        resp.id shouldEqual todos.head.id
      }
    }

    "fail if userId parameter is missing" in {
      Get("/todos") ~> Route.seal(router.route) ~> check {
        status shouldBe StatusCodes.NotFound
      }
    }
  }
}
