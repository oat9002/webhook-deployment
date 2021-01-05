package common

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding.Post
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpMethods, HttpRequest, HttpResponse, RequestEntity, StatusCodes}
import akka.http.scaladsl.unmarshalling.Unmarshal
import models._

import scala.concurrent.{ExecutionContext, Future}

trait DockerUtil {
  def validateRequest(webHook: DockerWebhook)(implicit ctx: ExecutionContext, system: ActorSystem): Future[Boolean]
}

class DockerUtilImpl extends DockerUtil with DockerCallbackJsonProtocol with DockerWebhookJsonProtocol {
  def validateRequest(webHook: DockerWebhook)(implicit ctx: ExecutionContext, system: ActorSystem): Future[Boolean] = {
    val response = for {
      reqEntity <- Marshal(webHook).to[RequestEntity]
      res <- Http().singleRequest(HttpRequest(
        uri = webHook.callbackUrl,
        method = HttpMethods.POST,
        entity = reqEntity)
      )
    } yield res
    val dockerCallback = response.flatMap {
      case res @ HttpResponse(StatusCodes.OK, _, _, _) => Unmarshal(res.entity).to[DockerCallback]
      case _ => Future.failed(throw new Exception("Cannot call back to docker"))
    }

    dockerCallback.map(dc => dc.state == "success")
  }
}

object DockerUtil {
  def apply(): DockerUtil = new DockerUtilImpl
}

