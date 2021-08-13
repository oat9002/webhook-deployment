package services

import akka.actor.ActorSystem
import common.{Commands, DockerUtil}
import models.DockerWebhook

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Success
import sys.process._

trait GoldPriceTrackingService {
  def deploy(): Boolean
}

class GoldPriceTrackingServiceImpl(lineService: LineService)(implicit ctx: ExecutionContext, system: ActorSystem) extends GoldPriceTrackingService {
  val lineMessage: String => String = lineService.prefixClassName(classOf[GoldPriceTrackingService])

  def deploy(): Boolean = {
    lineService.notify(lineMessage("Start Deployment"))

    val isError = Commands.goldPriceTrackingDeploy.map(_.!).exists(_ != 0)

    if (isError) {
      lineService.notify(lineMessage("Deployment is failed"))
      false
    } else {
      lineService.notify(lineMessage("Deployment is complete"))
      true
    }
  }
}

object GoldPriceTrackingService {
  def apply(lineService: LineService)(implicit ctx: ExecutionContext, system: ActorSystem): GoldPriceTrackingService = new GoldPriceTrackingServiceImpl(lineService)
}
