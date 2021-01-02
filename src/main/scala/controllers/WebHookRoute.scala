package controllers

import akka.actor.ActorSystem
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCode}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Directives, Route}
import common.Docker
import models.{DockerWebhook, DockerWebhookJsonProtocol}

import scala.concurrent.ExecutionContext
import scala.util.Success

class WebHookRoute(implicit ctx: ExecutionContext, actorSystem: ActorSystem) extends DockerWebhookJsonProtocol {
  val routes: Route = root ~ deploy

  def root: Route = pathEndOrSingleSlash {
    Directives.get {
      complete(HttpEntity(ContentTypes.`application/json`, "Welcome to webhook deployment"))
    }
  }

  def deploy: Route =
    pathPrefix("deploy") {
      concat(
        path("goldpricetracking") {
          concat(
            pathEndOrSingleSlash {
              Directives.post {
                entity(as[DockerWebhook]) { dockerWebHook =>
                  val result = Docker.validateRequest(dockerWebHook)
                  onComplete(result) {
                    case Success(true) => complete(StatusCode.int2StatusCode(200))
                    case Success(false) => complete(StatusCode.int2StatusCode(400))
                    case _ => complete(StatusCode.int2StatusCode(500))
                  }
                }
              }
            }
          )
        }
      )
    }
}

object WebHookRoute {
  def apply(implicit ctx: ExecutionContext, actorSystem: ActorSystem) = new WebHookRoute
}
