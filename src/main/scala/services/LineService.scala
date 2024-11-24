package services

import akka.actor.ActorSystem
import akka.http.scaladsl.model.FormData
import cats.effect.IO
import cats.effect.unsafe.implicits.global
import common.{Configuration, HttpClient}
import io.circe.generic.codec.DerivedAsObjectCodec.deriveCodec
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.headers.Authorization
import org.http4s.implicits.http4sLiteralsSyntax
import org.http4s.{Headers, Request}

import scala.concurrent.{ExecutionContext, Future}

trait LineService {
  def notify(message: String): Future[Boolean]
  def prefixClassName[T](c: Class[T])(text: String): String
}

class LineServiceImpl extends LineService {
  override def notify(message: String): Future[Boolean] = {

    val response = HttpClient.get.use { client =>
      val request = Request[IO](
        uri = uri"${Configuration.lineConfig.url}",
        method = org.http4s.Method.POST,
        headers = Headers(Authorization.parse(s" ${Configuration.lineConfig.lineNotifyToken}"))
      )
        .withEntity(FormData("message" -> message).toEntity)

      client.expect[String](request).attempt.map {
        case Right(_) => true
        case Left(_) => false
      }
    }

    response.unsafeToFuture()
  }

  override def prefixClassName[T](c: Class[T])(text: String): String = {
    s"[${c.getSimpleName}]: $text"
  }
}

object LineService {
  def apply(implicit ctx: ExecutionContext, system: ActorSystem): LineService = new LineServiceImpl()
}