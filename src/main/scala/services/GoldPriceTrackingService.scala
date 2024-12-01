package services

import cats.effect.IO
import com.typesafe.scalalogging.LazyLogging
import common.Commands

import scala.sys.process._

trait GoldPriceTrackingService {
  def deploy(): IO[Boolean]
}

class GoldPriceTrackingServiceImpl(lineService: LineService) extends GoldPriceTrackingService with LazyLogging {
  val lineMessage: String => String = lineService.prefixClassName(classOf[GoldPriceTrackingService])

  def deploy(): IO[Boolean] = {
    lineService.notify(lineMessage("Start Deployment")).flatMap { result => {
      if (result) {
        val isError = Commands.goldPriceTrackingDeploy.map(_.!).exists(_ != 0)

        if (isError) {
          lineService.notify(lineMessage("Deployment is failed"))
        } else {
          lineService.notify(lineMessage("Deployment is complete"))
        }
      } else {
        lineService.notify(lineMessage("Deployment is failed"))
      }
    }}
  }
}

object GoldPriceTrackingService {
  def apply(lineService: LineService): GoldPriceTrackingService = new GoldPriceTrackingServiceImpl(lineService)
}
