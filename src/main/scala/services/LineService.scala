package services

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model.{HttpHeader, HttpMethods, HttpRequest, HttpResponse, RequestEntity, StatusCodes, headers}
import common.Configuration
import models.requests.{LineNotifyRequest, LineNotifyRequestJsonProtocol}

import scala.concurrent.{ExecutionContext, Future}

trait LineService {
  def notify(message: String): Future[Boolean]
}

class LineServiceImpl(implicit ctx: ExecutionContext, system: ActorSystem) extends LineService with LineNotifyRequestJsonProtocol {
  override def notify(message: String): Future[Boolean] = {
    val response = for {
      reqEntity <- Marshal(LineNotifyRequest(message)).to[RequestEntity]
      res <- Http().singleRequest(HttpRequest(
        uri = Configuration.lineConfig.url,
        method = HttpMethods.POST,
        entity = reqEntity,
        headers = Seq[HttpHeader](RawHeader("Authorization", s"Bearer ${Configuration.lineConfig.lineNotifyToken}"))
      ))
    } yield res

    response.map {
      case HttpResponse(StatusCodes.OK, _, _, _) => true
      case _ => false
    }
  }
}

object LineService {
  def apply(implicit ctx: ExecutionContext, system: ActorSystem): LineService = new LineServiceImpl()
}