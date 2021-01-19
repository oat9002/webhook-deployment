package services

import akka.actor.ActorSystem
import common.{Commands, DockerUtil}
import models.DockerWebhook

import scala.concurrent.{ExecutionContext, Future}
import scala.io.Source
import sys.process._

trait GoldPriceTrackingService {
  def deploy(dockerWebHook: Option[DockerWebhook]): Future[Boolean]
}

class GoldPriceTrackingServiceImpl(dockerUtil: DockerUtil)(implicit ctx: ExecutionContext, system: ActorSystem) extends GoldPriceTrackingService {
  def deploy(dockerWebHook: Option[DockerWebhook]): Future[Boolean] = {
    dockerWebHook match {
      case Some(d) =>
        val result = Commands.goldPriceTrackingDeploy.map(_.!).exists(_ != 0)

        if (result) {
          dockerUtil.callback(d)
        } else {
          Future.successful(false)
        }
      case _ => Future.successful(false)
    }
  }
}

object GoldPriceTrackingService {
  def apply(dockerUtil: DockerUtil)(implicit ctx: ExecutionContext, system: ActorSystem): GoldPriceTrackingService = new GoldPriceTrackingServiceImpl(dockerUtil)
}
