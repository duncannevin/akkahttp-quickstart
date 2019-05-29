import akka.actor.ActorSystem

import scala.util.{Failure, Success}
import akka.stream.ActorMaterializer
import db.DbConfiguration
import entities.Todo
import logging.TodoLogger
import repository.{Repository, TodoRepository}
import routes.{Router, TodoRouter}

import scala.concurrent.Await
import scala.concurrent.duration.FiniteDuration

object Main extends App with TodoLogger with DbConfiguration {
  val host = "0.0.0.0"
  val port = 9000

  implicit val system: ActorSystem = ActorSystem(name = "todo-api")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  import system.dispatcher

  val todoRepository: Repository[Todo] = new TodoRepository(dbConfig)
  val router: Router = new TodoRouter(todoRepository)
  val server: Server = new Server(router, host, port)

  val binding = server.bind()
  binding.onComplete {
    case Success(_) => serverListening(port)
    case Failure(error) => startFailure(port, error.getMessage)
  }
  Await.result(binding, FiniteDuration(3, "seconds"))
}
