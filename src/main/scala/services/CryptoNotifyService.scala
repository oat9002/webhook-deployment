package services

trait CryptoNotifyService {
  def deploy(): Unit
}

class CryptoNotifyServiceImpl(lineService: LineService) extends CryptoNotifyService {
  override def deploy(): Unit = ???
}
