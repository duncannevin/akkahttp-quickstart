import akka.NotUsed
import akka.actor.ActorSystem
import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow
import routes.Router

import scala.concurrent.{ExecutionContext, Future}

class Server(router: Flow[HttpRequest, HttpResponse, NotUsed], host: String, port: Int)
            (implicit system: ActorSystem, ex: ExecutionContext, mat: ActorMaterializer) {
  def bind(): Future[ServerBinding] = Http().bindAndHandle(router, host, port)
}
