package services

import akka.actor.ActorSystem
import common.DockerUtil
import models.DockerWebhook

import scala.concurrent.{ExecutionContext, Future}
import scala.io.Source
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
      val execCommands = Source.fromResource("gold-price-tracking-server-deploy.sh").getLines().toList
      val result = execCommands.slice(1, execCommands.length).map(cmd => cmd.!)

      !result.contains(1)
    } else false)
  }
}

object GoldPriceTrackingService {
  def apply(dockerUtil: DockerUtil)(implicit ctx: ExecutionContext, system: ActorSystem): GoldPriceTrackingService = new GoldPriceTrackingServiceImpl(dockerUtil)
}
