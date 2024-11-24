package services

import common.Commands

import scala.sys.process._

trait GoldPriceTrackingService {
  def deploy(): Boolean
}

class GoldPriceTrackingServiceImpl(lineService: LineService) extends GoldPriceTrackingService {
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
  def apply(lineService: LineService): GoldPriceTrackingService = new GoldPriceTrackingServiceImpl(lineService)
}
