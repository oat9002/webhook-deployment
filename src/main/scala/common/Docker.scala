package common

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding.Post
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.unmarshalling.Unmarshal
import models._

import scala.concurrent.{ExecutionContext, Future}

object Docker extends DockerCallbackJsonProtocol with DockerWebhookJsonProtocol {
  def validateRequest(webHook: DockerWebhook)(implicit ctx: ExecutionContext, system: ActorSystem): Future[Boolean] = {
    val request = Post(webHook.callbackUrl, webHook)
    val response = Http().singleRequest(request)
    val dockerCallback = response.flatMap {
      case res @ HttpResponse(StatusCodes.OK, _, _, _) => Unmarshal(res.entity).to[DockerCallback]
      case _ => Future.failed(throw new Exception("Cannot call back to docker"))
    }

    dockerCallback.map(dc => dc.state == "success")
  }
}
