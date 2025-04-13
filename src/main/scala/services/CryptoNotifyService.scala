package services

import cats.effect.IO
import com.typesafe.scalalogging.LazyLogging
import common.Configuration

import java.io.File
import scala.sys.process._

trait CryptoNotifyService extends DeploymentService {
  def deploy(): IO[Boolean]
}

class CryptoNotifyServiceImpl(val telegramService: TelegramService)
    extends CryptoNotifyService
    with LazyLogging {
  private val message: String => String =
    telegramService.prefixClassName(classOf[CryptoNotifyService])
  private val cryptoNotifyDeployCommand: String =
    s"sh ${Configuration.cryptoNotifyConfig.folderPath}/deploy.sh"

  override def deploy(): IO[Boolean] = {
    deployWithNotifyMessage(
      message,
      () => {
        val workDirectory =
          new File(Configuration.cryptoNotifyConfig.folderPath)

        Process(cryptoNotifyDeployCommand, workDirectory).! != 0
      }
    )
  }
}

object CryptoNotifyService {
  def apply(telegramService: TelegramService): CryptoNotifyService =
    new CryptoNotifyServiceImpl(telegramService)
}
