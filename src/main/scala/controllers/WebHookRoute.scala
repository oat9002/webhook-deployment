package controllers

import cats.effect.IO
import cats.implicits.toSemigroupKOps
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.server.Router
import org.http4s.server.middleware.Timeout
import services.{GoldPriceTrackingService, TelegramService}

import scala.concurrent.duration.DurationInt

class WebHookRoute {
  val telegramService: TelegramService = TelegramService()
  val goldPriceTrackingService: GoldPriceTrackingService = GoldPriceTrackingService(telegramService)

  private val root: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root => Ok("Welcome to webhook deployment")
  }

  private val deploy: AuthedRoutes[Boolean, IO] = AuthedRoutes.of {
    case GET -> Root /  "goldpricetracking" as isAuthed => {
      if (!isAuthed) {
        Forbidden("Unauthorized!")
      } else {
        goldPriceTrackingService.deploy().flatMap {
          case true => Ok("Deployment is complete")
          case _ => InternalServerError("Deployment is failed")
        }
      }
    }
  }

  val route: HttpRoutes[IO] = Router(
    "docker/deploy" -> (root <+> Timeout.httpRoutes(10.minutes)(AuthenticationMiddleware.apply(deploy)))
  )
}

object WebHookRoute {
  def apply() = new WebHookRoute
}
