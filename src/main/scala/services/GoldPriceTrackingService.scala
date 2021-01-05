package services

import akka.actor.ActorSystem
import common.DockerUtil
import models.DockerWebhook

import scala.concurrent.{ExecutionContext, Future}
import sys.process._

trait GoldPriceTrackingService {
  def deploy(dockerWebHook: Option[DockerWebhook]): Future[Boolean]
}

class GoldPriceTrackingServiceImpl(dockerUtil: DockerUtil)(implicit ctx: ExecutionContext, system: ActorSystem) extends GoldPriceTrackingService {
  def deploy(dockerWebHook: Option[DockerWebhook]): Future[Boolean] = {
    val isValid = dockerWebHook match {
      case Some(x) => dockerUtil.validateRequest(x)
      case _ => Future(false)
    }

    isValid.map(if (_) {
      val execFilePath = getClass.getResource("/gold-price-tracking-server.sh").getPath
      val result = execFilePath.!

      result == 0
    } else false)
  }
}

object GoldPriceTrackingService {
  def apply(dockerUtil: DockerUtil)(implicit ctx: ExecutionContext, system: ActorSystem): GoldPriceTrackingService = new GoldPriceTrackingServiceImpl(dockerUtil)
}
