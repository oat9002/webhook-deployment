package services

import cats.effect.IO
import com.typesafe.scalalogging.LazyLogging
import common.Commands

import scala.sys.process._

trait GoldPriceTrackingService {
  def deploy(): IO[Boolean]
}

class GoldPriceTrackingServiceImpl(telegramService: TelegramService)
    extends GoldPriceTrackingService
    with LazyLogging {
  val message: String => String =
    telegramService.prefixClassName(classOf[GoldPriceTrackingService])

  def deploy(): IO[Boolean] = {
    telegramService.notify(message("Start Deployment")).flatMap { result =>
      {
        if (result) {
          val isError = Commands.goldPriceTrackingDeploy.map(_.!).exists(_ != 0)

          if (isError) {
            telegramService.notify(message("Deployment is failed"))
          } else {
            telegramService.notify(message("Deployment is complete"))
          }
        } else {
          telegramService.notify(message("Deployment is failed"))
        }
      }
    }
  }
}

object GoldPriceTrackingService {
  def apply(telegramService: TelegramService): GoldPriceTrackingService =
    new GoldPriceTrackingServiceImpl(telegramService)
}
