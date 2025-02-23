package services

import cats.effect.IO
import com.typesafe.scalalogging.LazyLogging
import common.{Configuration, HttpClient}
import io.circe.generic.codec.DerivedAsObjectCodec.deriveCodec
import org.http4s.circe.CirceEntityCodec.circeEntityEncoder
import org.http4s.headers.{Authorization, `Content-Type`}
import org.http4s.{Headers, MediaType, Request, Uri, UrlForm}

import java.net.URLEncoder

trait TelegramService {
  def notify(message: String): IO[Boolean]
  def prefixClassName[T](c: Class[T])(text: String): String
}

class TelegramServiceImpl extends TelegramService with LazyLogging {
  private val charset = "UTF-8"
  private val baseUrl = ""

  override def notify(message: String): IO[Boolean] = {
    val botToken = Configuration.telegramConfig.botToken
    val chatId = Configuration.telegramConfig.chatId
    val textParam = s"text=${URLEncoder.encode(message, charset)}"
    val chatIdParam = s"chat_id=${URLEncoder.encode(chatId, charset)}"
    val parseModeParam = s"parse_mode=HTML"
    val url =
      s"$baseUrl/bot$botToken/sendMessage?$textParam&$chatIdParam&$parseModeParam"

    val response = HttpClient.get.use { client =>
      val request = Request[IO](
        uri = Uri.fromString(url) match {
          case Right(uri) => uri
          case _ => throw new Exception("Invalid URL")
        },
        method = org.http4s.Method.GET,
        headers = Headers(`Content-Type`(MediaType.application.`x-www-form-urlencoded`))
      )
        .withEntity(UrlForm("message" -> message))

      client.expect[String](request).attempt.map {
        case Right(_) => true
        case Left(ex) =>
          logger.error(s"Error sending message to Telegram: ${ex.getMessage}")
          false
      }
    }

    response
  }

  override def prefixClassName[T](c: Class[T])(text: String): String = {
    s"[${c.getSimpleName}]: $text"
  }
}

object TelegramService {
  def apply(): TelegramService = new TelegramServiceImpl()
}