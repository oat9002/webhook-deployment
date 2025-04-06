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
  lazy val appConfig: AppConfig =
    AppConfig(appSection.getString("apiKey"), appSection.getInt("port"))
  lazy val telegramConfig: TelegramConfig = TelegramConfig(
    telegramSection.getString("botToken"),
    telegramSection.getString("chatId")
  )
  lazy val firebase: FirebaseConfig = FirebaseConfig(
    firebaseSection.getString("secret")
  )
}

case class AppConfig(apiKey: String, port: Int)
case class TelegramConfig(botToken: String, chatId: String)
case class FirebaseConfig(secret: String)
