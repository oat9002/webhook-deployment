package services

import cats.effect.IO
import com.typesafe.scalalogging.LazyLogging
import common.Commands

import sys.process._

trait CryptoNotifyService {
  def deploy(): IO[Boolean]
}

class CryptoNotifyServiceImpl(telegramService: TelegramService)
    extends CryptoNotifyService
    with LazyLogging {
  private val message: String => String =
    telegramService.prefixClassName(classOf[CryptoNotifyService])

  override def deploy(): IO[Boolean] = {
    telegramService.notify(message("Start Deployment")).flatMap { isSuccess =>
      if (isSuccess) {
        val isError = Commands.cryptoNotifyDeploy.map(_.!).exists(_ != 0)

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

object CryptoNotifyService {
  def apply(telegramService: TelegramService): CryptoNotifyService =
    new CryptoNotifyServiceImpl(telegramService)
}
