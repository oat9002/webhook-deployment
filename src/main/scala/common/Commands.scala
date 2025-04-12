package common

object Commands {
  val goldPriceTrackingDeploy: Seq[String] = List(
    s"sh ${Configuration.goldPriceTrackingConfig.folderPath}/server/deploy.sh"
  )
  val cryptoNotifyDeploy: Seq[String] = List(
    s"sh ${Configuration.cryptoNotifyConfig.folderPath}/deploy.sh"
  )
}
