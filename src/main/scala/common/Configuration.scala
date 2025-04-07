package common

import com.typesafe.config.{Config, ConfigFactory}

object Configuration {
  private val conf: Config = {
    val c = ConfigFactory.load()

    if (EnvironmentHelper.isDevelopment) {
      val toReturn = ConfigFactory.load("application.local")
      ConfigFactory.invalidateCaches()
      toReturn
    } else {
      c
    }
  }
  private val appSection = conf.getConfig("app")
  private val telegramSection = conf.getConfig("telegram")
  private val firebaseSection = conf.getConfig("firebase")
  private val goldPriceTrackingSection = conf.getConfig("goldpricetracking")
  private val cryptoNotifySection = conf.getConfig("cryptonotify")
  lazy val appConfig: AppConfig =
    AppConfig(appSection.getString("apiKey"), appSection.getInt("port"))
  lazy val telegramConfig: TelegramConfig = TelegramConfig(
    telegramSection.getString("botToken"),
    telegramSection.getString("chatId")
  )
  lazy val firebase: FirebaseConfig = FirebaseConfig(
    firebaseSection.getString("secret")
  )
  lazy val goldPriceTrackingConfig: GoldPriceTrackingConfig =
    GoldPriceTrackingConfig(
      goldPriceTrackingSection.getString("folderPath")
    )
  lazy val cryptoNotifyConfig: CryptoNotifyConfig =
    CryptoNotifyConfig(
      cryptoNotifySection.getString("folderPath")
    )
}

case class AppConfig(apiKey: String, port: Int)
case class TelegramConfig(botToken: String, chatId: String)
case class FirebaseConfig(secret: String)
case class GoldPriceTrackingConfig(folderPath: String)
case class CryptoNotifyConfig(folderPath: String)
