package services

import cats.effect.IO
import common.{Configuration, HttpClient}
import io.circe.generic.codec.DerivedAsObjectCodec.deriveCodec
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.headers.{Authorization, `Content-Type`}
import org.http4s.{Headers, MediaType, Request, Uri, UrlForm}

trait LineService {
  def notify(message: String): IO[Boolean]
  def prefixClassName[T](c: Class[T])(text: String): String
}

class LineServiceImpl extends LineService {
  override def notify(message: String): IO[Boolean] = {

    val response = HttpClient.get.use { client =>
      val request = Request[IO](
        uri = Uri.fromString(s"${Configuration.lineConfig.url}") match {
          case Right(uri) => uri
          case _ => throw new Exception("Invalid URL")
        },
        method = org.http4s.Method.POST,
        headers = Headers(Authorization.parse(s" ${Configuration.lineConfig.lineNotifyToken}"), `Content-Type`(MediaType.application.`x-www-form-urlencoded`))
      )
        .withEntity(UrlForm("message" -> message))

      client.expect[String](request).attempt.map {
        case Right(_) => true
        case Left(_) => false
      }
    }

    response
  }

  override def prefixClassName[T](c: Class[T])(text: String): String = {
    s"[${c.getSimpleName}]: $text"
  }
}

object LineService {
  def apply(): LineService = new LineServiceImpl()
}