package services

import common.Commands

import sys.process._

trait CryptoNotifyService {
  def deploy(): Boolean
}

class CryptoNotifyServiceImpl(lineService: LineService) extends CryptoNotifyService {
  override def deploy(): Boolean = {
    val lineMessage: String => String = lineService.prefixClassName(classOf[CryptoNotifyService])

    lineService.notify(lineMessage("Start Deployment"))

    val isError = Commands.cryptoNotifyDeploy.map(_.!).exists(_ != 0)

    if (isError) {
      lineService.notify(lineMessage("Deployment is failed"))
      false
    } else {
      lineService.notify(lineMessage("Deployment is complete"))
      true
    }
  }
}

object CryptoNotifyService {
  def apply(lineService: LineService): CryptoNotifyService = new CryptoNotifyServiceImpl(lineService)
}
