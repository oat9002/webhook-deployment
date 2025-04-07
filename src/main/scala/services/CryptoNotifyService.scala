package services

import cats.effect.IO
import common.Commands

import sys.process._

trait CryptoNotifyService {
  def deploy(): IO[Boolean]
}

class CryptoNotifyServiceImpl(telegramService: TelegramService)
    extends CryptoNotifyService {
  override def deploy(): IO[Boolean] = {
    val message: String => String =
      telegramService.prefixClassName(classOf[CryptoNotifyService])

    telegramService.notify(message("Start Deployment"))

    val isError = Commands.cryptoNotifyDeploy.map(_.!).exists(_ != 0)

    if (isError) {
      telegramService.notify(message("Deployment is failed"))
    } else {
      telegramService.notify(message("Deployment is complete"))
    }
  }
}

object CryptoNotifyService {
  def apply(telegramService: TelegramService): CryptoNotifyService =
    new CryptoNotifyServiceImpl(telegramService)
}
