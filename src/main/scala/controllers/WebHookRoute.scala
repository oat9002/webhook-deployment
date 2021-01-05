package controllers

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCode}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Directives, Route}
import common.DockerUtil
import models.{DockerWebhook, DockerWebhookJsonProtocol}
import services.GoldPriceTrackingService

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

class WebHookRoute(implicit ctx: ExecutionContext, actorSystem: ActorSystem) extends DockerWebhookJsonProtocol {
  val routes: Route = root ~ deploy
  val dockerUtil: DockerUtil = DockerUtil()
  val goldPricetrackingService: GoldPriceTrackingService = GoldPriceTrackingService(dockerUtil)

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
                        case Success(true) => complete(StatusCode.int2StatusCode(200))
                        case Success(false) => complete(StatusCode.int2StatusCode(400))
                        case Failure(x) => complete(StatusCode.int2StatusCode(500), x.getMessage)
                      }
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
