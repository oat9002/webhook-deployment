package controllers


import cats.effect.IO
import cats.implicits.toSemigroupKOps
import models.DockerWebhookJsonProtocol
import org.http4s.HttpRoutes
import services.{CryptoNotifyService, GoldPriceTrackingService, LineService}
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.server.Router

import scala.concurrent.duration.DurationInt


class WebHookRoute extends DockerWebhookJsonProtocol {
  val lineService: LineService = LineService()
  val goldPriceTrackingService: GoldPriceTrackingService = GoldPriceTrackingService(lineService)
  val cryptoNotifyService: CryptoNotifyService = CryptoNotifyService(lineService)

  private val root: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root => Ok("Welcome to webhook deployment")
  }

  private val deploy: AuthedRoutes[Boolean, IO] = AuthedRoutes.of {
    case GET -> Root /  "goldpricetracking" as isAuthed => {
      if (!isAuthed) {
        Forbidden("Unauthorized!")
      }
      else if (goldPriceTrackingService.deploy()) {
        Ok("Deployment is complete")
      } else {
        InternalServerError("Deployment is failed")
      }
    }
  }

  val route: HttpRoutes[IO] = Router(
    "/" -> (root <+> RequestTimeoutMiddleware.apply(10.minutes)(AuthenticationMiddleware.apply(deploy)))
  )
}

object WebHookRoute {
  def apply() = new WebHookRoute
}
