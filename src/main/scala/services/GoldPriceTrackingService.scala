package services

import akka.actor.ActorSystem
import common.{Commands, DockerUtil}
import models.DockerWebhook

import scala.concurrent.{ExecutionContext, Future}
import scala.io.Source
import scala.util.Success
import sys.process._

trait GoldPriceTrackingService {
  def deploy(dockerWebHook: Option[DockerWebhook]): Future[Boolean]
}

class GoldPriceTrackingServiceImpl(dockerUtil: DockerUtil, lineService: LineService)(implicit ctx: ExecutionContext, system: ActorSystem) extends GoldPriceTrackingService {
  val lineMessage: String => String = lineService.prefixClassName(classOf[GoldPriceTrackingService])
  def deploy(dockerWebHook: Option[DockerWebhook]): Future[Boolean] = {
    lineService.notify(lineMessage("Start Deployment"))

    val result = dockerWebHook match {
      case Some(d) =>
        val isError = Commands.goldPriceTrackingDeploy.map(_.!).exists(_ != 0)

        if (isError) {
          Future.successful(false)
          //dockerUtil.callback(d)
        } else {
          Future.successful(true)
        }
      case _ => Future.successful(false)
    }

    result andThen {
      case Success(true) => lineService.notify(lineMessage("Deployment is complete"))
      case _ => lineService.notify(lineMessage("Deployment is failed"))
    }
  }
}

object GoldPriceTrackingService {
  def apply(dockerUtil: DockerUtil, lineService: LineService)(implicit ctx: ExecutionContext, system: ActorSystem): GoldPriceTrackingService = new GoldPriceTrackingServiceImpl(dockerUtil, lineService)
}
