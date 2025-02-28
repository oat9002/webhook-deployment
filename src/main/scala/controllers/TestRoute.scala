package controllers

import cats.effect.IO
import common.EnvironmentHelper
import org.http4s.HttpRoutes
import org.http4s.dsl.io._
import services.TelegramService

class TestRoute {
  val telegramService: TelegramService = TelegramService()

  val route: HttpRoutes[IO] = if (EnvironmentHelper.isDevelopment) {
    HttpRoutes.of[IO] {
      case GET -> Root => Ok("Welcome to test route")
      case GET -> Root / "test" / "notify" =>
        telegramService.notify("Test Notification").flatMap {
          case true => Ok("Notification is sent")
          case _    => Ok("Notification is failed")
        }
    }
  } else {
    HttpRoutes.of[IO] { case GET -> Root =>
      NotAcceptable("route not allowed")
    }
  }
}

object TestRoute {
  def apply() = new TestRoute()
}
