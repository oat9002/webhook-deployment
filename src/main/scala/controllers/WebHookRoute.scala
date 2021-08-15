package controllers

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import models.DockerWebhookJsonProtocol
import services.{CryptoNotifyService, GoldPriceTrackingService, LineService}

import scala.concurrent.ExecutionContext

class WebHookRoute(implicit ctx: ExecutionContext, actorSystem: ActorSystem) extends DockerWebhookJsonProtocol {
  val authenticationRoute: AuthenticationRoute = AuthenticationRoute()
  val routes: Route = authenticationRoute.routes ~ root ~ deploy
  val lineService: LineService = LineService(ctx, actorSystem)
  val goldPriceTrackingService: GoldPriceTrackingService = GoldPriceTrackingService(lineService)
  val cryptoNotifyService: CryptoNotifyService = CryptoNotifyService(lineService)

  def root: Route = pathEndOrSingleSlash {
    get {
      complete(HttpEntity(ContentTypes.`application/json`, "Welcome to webhook deployment"))
    }
  }

  def deploy: Route =
    pathPrefix("docker") {
      concat(
        pathPrefix("deploy") {
          concat(
            path("goldpricetracking") {
              concat(
                pathEndOrSingleSlash {
                  get {
                    if (goldPriceTrackingService.deploy()) {
                      complete(StatusCodes.OK)
                    } else {
                      complete(StatusCodes.InternalServerError)
                    }
                  }
                }
              )
            },
            path("cryptonotify") {
              concat(
                pathEndOrSingleSlash {
                  get {
                    if (cryptoNotifyService.deploy()) {
                      complete(StatusCodes.OK)
                    } else {
                      complete(StatusCodes.InternalServerError)
                    }
                  }
                }
              )
            }
          )
        }
      )
    }
}

object WebHookRoute {
  def apply(implicit ctx: ExecutionContext, actorSystem: ActorSystem) = new WebHookRoute
}
