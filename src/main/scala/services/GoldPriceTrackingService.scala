package services

import sys.process._

class GoldPriceTrackingService {
  def deploy(): Boolean = {
    val execFilePath = getClass.getResource("/gold-price-tracking-server.sh").getPath
    val result = execFilePath.!

    result == 0
  }
}

object GoldPriceTrackingService {
  def apply(): GoldPriceTrackingService = new GoldPriceTrackingService()
}
