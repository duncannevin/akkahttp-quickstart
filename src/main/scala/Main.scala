import akka.actor.ActorSystem

import scala.util.{Failure, Success}
import akka.stream.ActorMaterializer
import logging.TodoLogger
import repository.InMemoryTodoRepository
import routes.TodoRouter

import scala.concurrent.Await
import scala.concurrent.duration.FiniteDuration

object Main extends App with TodoLogger {
  val host = "0.0.0.0"
  val port = 9000

  implicit val system: ActorSystem = ActorSystem(name = "todo-api")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  import system.dispatcher

  val todoRepository = new InMemoryTodoRepository()
  val router = new TodoRouter(todoRepository)
  val server = new Server(router, host, port)

  val binding = server.bind()
  binding.onComplete {
    case Success(_) => serverListening(port)
    case Failure(error) => startFailure(port, error.getMessage)
  }
  Await.result(binding, FiniteDuration(3, "seconds"))
}
