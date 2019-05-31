import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow
import db.DbConfiguration
import entities.{Todo, User}
import logging.TodoLogger
import repository.{Repository, TodoRepository, UserRepository}
import routes.{Router, TodoRouter, UserRouter}

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.FiniteDuration
import scala.util.{Failure, Success}

object Main extends App with TodoLogger with DbConfiguration {
  val host = "0.0.0.0"
  val port = 9000

  implicit val system: ActorSystem = ActorSystem(name = "todo-api")
  implicit val materializer: ActorMaterializer = ActorMaterializer()

  val todoRepository: Repository[Todo] = new TodoRepository(db)
  todoRepository.init()
  val userRepository: Repository[User] = new UserRepository(db)
  todoRepository.init()
  val todoRouter: Router = new TodoRouter(todoRepository)
  val userRouter: Router = new UserRouter(userRepository)

  lazy val routes: Route = Seq(todoRouter, userRouter).foldRight[Route](reject) {
    (partial, builder) =>
      partial.route ~ builder
  }

  lazy val router: Flow[HttpRequest, HttpResponse, NotUsed] =
    Route.handlerFlow(routes)

  val server: Server = new Server(router, host, port)

  val binding = server.bind()

  binding.onComplete {
    case Success(_) => serverListening(port)
    case Failure(error) => startFailure(port, error.getMessage)
  }
  Await.result(binding, FiniteDuration(3, "seconds"))
}
