package common

import com.typesafe.config.{Config, ConfigFactory}

object Configuration {
  private val conf: Config = {
    val c = ConfigFactory.load()
    val env = System.getenv().getOrDefault("env", "development")
    if (!"production".equals(env)) {
      val toReturn = ConfigFactory.load("application.local")
      ConfigFactory.invalidateCaches()
      toReturn
    } else {
      c
    }
  }
  private val appSection = conf.getConfig("app")
  lazy val appConfig: AppConfig = AppConfig(appSection.getString("env"), appSection.getString("apiKey"), appSection.getInt("port"))
  lazy val telegramConfig: TelegramConfig = TelegramConfig("botToken", "chatId")
}

case class AppConfig(env: String, apiKey: String, port: Int)
case class TelegramConfig(botToken: String, chatId: String)
