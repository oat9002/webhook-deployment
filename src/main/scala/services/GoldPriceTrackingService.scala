package services

import cats.effect.IO
import com.typesafe.scalalogging.LazyLogging
import common.Configuration

import java.io.File
import scala.sys.process._

trait GoldPriceTrackingService extends DeploymentService {
  def deploy(): IO[Boolean]
}

class GoldPriceTrackingServiceImpl(val telegramService: TelegramService)
    extends GoldPriceTrackingService
    with LazyLogging {
  private val message: String => String =
    telegramService.prefixClassName(classOf[GoldPriceTrackingService])
  private val goldPriceTrackingDeployCommand: String =
    s"sh ${Configuration.goldPriceTrackingConfig.folderPath}/server/deploy.sh"

  def deploy(): IO[Boolean] = {
    deployWithNotifyMessage(
      message,
      () => {
        val workDirectory =
          new File(Configuration.goldPriceTrackingConfig.folderPath)

        Process(goldPriceTrackingDeployCommand, workDirectory).! != 0
      }
    )
  }
}

object GoldPriceTrackingService {
  def apply(telegramService: TelegramService): GoldPriceTrackingService =
    new GoldPriceTrackingServiceImpl(telegramService)
}
