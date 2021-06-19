package controllers

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCode, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Directives, Route}
import common.DockerUtil
import models.{DockerWebhook, DockerWebhookJsonProtocol}
import services.{CryptoNotifyService, GoldPriceTrackingService, LineService}

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

class WebHookRoute(implicit ctx: ExecutionContext, actorSystem: ActorSystem) extends DockerWebhookJsonProtocol {
  val routes: Route = root ~ deploy
  val dockerUtil: DockerUtil = DockerUtil(ctx, actorSystem)
  val lineService: LineService = LineService(ctx, actorSystem)
  val goldPricetrackingService: GoldPriceTrackingService = GoldPriceTrackingService(dockerUtil, lineService)
  val cryptoNotifyService: CryptoNotifyService = CryptoNotifyService(lineService)

  def root: Route = pathEndOrSingleSlash {
    Directives.get {
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
                  Directives.post {
                    entity(as[Option[DockerWebhook]]) { dockerWebHook =>
                      val result = goldPricetrackingService.deploy(dockerWebHook)
                      onComplete(result) {
                        case Success(true) => complete(StatusCodes.OK)
                        case Success(false) => complete(StatusCodes.BadRequest)
                        case Failure(x) => complete(StatusCodes.InternalServerError, x.getMessage)
                      }
                    }
                  }
                }
              )
            },
            path("cryptonotify") {
              concat(
                pathEndOrSingleSlash {
                  Directives.get {
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
