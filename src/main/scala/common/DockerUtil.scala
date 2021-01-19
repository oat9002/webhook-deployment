package common

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, HttpResponse, RequestEntity, StatusCodes}
import akka.http.scaladsl.unmarshalling.Unmarshal
import models._

import scala.concurrent.{ExecutionContext, Future}

trait DockerUtil {
  def callback(webHook: DockerWebhook)(implicit ctx: ExecutionContext, system: ActorSystem): Future[Boolean]
}

class DockerUtilImpl extends DockerUtil with DockerCallbackJsonProtocol with DockerWebhookJsonProtocol {
  def callback(webHook: DockerWebhook)(implicit ctx: ExecutionContext, system: ActorSystem): Future[Boolean] = {
    val response = for {
      reqEntity <- Marshal(DockerCallbackRequest("success", "PASS", "Deploy gold price tracking server", webHook.callbackUrl)).to[RequestEntity]
      res <- Http().singleRequest(HttpRequest(
        uri = webHook.callbackUrl,
        method = HttpMethods.POST,
        entity = reqEntity
      ))
    } yield res

    response.flatMap {
      case HttpResponse(StatusCodes.OK, _, _, _) => Future.successful(true)
      case _ => Future.failed(throw new Exception("Cannot call back to docker"))
    }
  }
}

object DockerUtil {
  def apply(): DockerUtil = new DockerUtilImpl
}

