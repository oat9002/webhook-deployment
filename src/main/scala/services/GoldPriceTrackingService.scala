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
    dockerWebHook match {
      case Some(d) =>
        val filePath = getClass.getClassLoader.getResource("gold-price-tracking-server-deploy.sh").getPath
        val result = s"/bin/bash $filePath".!

        if (result == 0) {
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
