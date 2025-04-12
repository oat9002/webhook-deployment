package common

object Commands {
  val goldPriceTrackingDeploy: Seq[String] = List(
    "sh ${Configuration.goldPriceTrackingConfig.folderPath}/server/deploy.sh"
  )
  val cryptoNotifyDeploy: Seq[String] = List(
    "sh ${Configuration.cryptoNotifyConfig.folderPath}/deploy.sh"
  )
}
