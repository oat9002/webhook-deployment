package services

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.model.{FormData, HttpHeader, HttpMethods, HttpRequest, HttpResponse, StatusCodes}
import common.Configuration

import scala.concurrent.{ExecutionContext, Future}

trait LineService {
  def notify(message: String): Future[Boolean]
  def prefixClassName[T](c: Class[T])(text: String): String
}

class LineServiceImpl(implicit ctx: ExecutionContext, system: ActorSystem) extends LineService {
  override def notify(message: String): Future[Boolean] = {
    val response = Http().singleRequest(HttpRequest(
        uri = Configuration.lineConfig.url,
        method = HttpMethods.POST,
        entity = FormData(Map("message" -> message)).toEntity,
        headers = Seq[HttpHeader](RawHeader("Authorization", s"Bearer ${Configuration.lineConfig.lineNotifyToken}"))
      ))


    response.flatMap {
      case HttpResponse(StatusCodes.OK, _, entity, _) => entity.discardBytes().future().map(_ => true)
      case _ => Future.successful(false)
    }
  }

  override def prefixClassName[T](c: Class[T])(text: String): String = {
    s"[${c.getSimpleName}]: $text"
  }
}

object LineService {
  def apply(implicit ctx: ExecutionContext, system: ActorSystem): LineService = new LineServiceImpl()
}