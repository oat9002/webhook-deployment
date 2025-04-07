package common

object Commands {
  val goldPriceTrackingDeploy: Seq[String] = List(
    "docker stop server_gold-price-tracking-server_1",
    "docker rm server_gold-price-tracking-server_1",
    "docker image rm oat9002/gold-price-tracking-server",
    s"docker-compose -f ${Configuration.goldPriceTrackingConfig.folderPath}/server/docker-compose.yml up -d"
  )
  val cryptoNotifyDeploy: Seq[String] = List(
    "docker stop crypto-notify_crypto-notify_1",
    "docker rm crypto-notify_crypto-notify_1",
    "docker image rm oat9002/crypto-notify",
    s"${Configuration.cryptoNotifyConfig.folderPath}/docker-compose.yml up -d"
  )
}
