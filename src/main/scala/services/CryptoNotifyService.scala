package services

import common.Commands

import sys.process._

trait CryptoNotifyService {
  def deploy(): Boolean
}

class CryptoNotifyServiceImpl(telegramService: TelegramService)
    extends CryptoNotifyService {
  override def deploy(): Boolean = {
    val message: String => String =
      telegramService.prefixClassName(classOf[CryptoNotifyService])

    telegramService.notify(message("Start Deployment"))

    val isError = Commands.cryptoNotifyDeploy.map(_.!).exists(_ != 0)

    if (isError) {
      telegramService.notify(message("Deployment is failed"))
      false
    } else {
      telegramService.notify(message("Deployment is complete"))
      true
    }
  }
}

object CryptoNotifyService {
  def apply(telegramService: TelegramService): CryptoNotifyService =
    new CryptoNotifyServiceImpl(telegramService)
}
