package common

import com.typesafe.config.{Config, ConfigFactory}

object Configuration {
  private val conf: Config = ConfigFactory.load()
  private val lineSection = conf.getConfig("line")
  private val appSection = conf.getConfig("app")
  lazy val lineConfig: LineConfig = LineConfig(lineSection.getString("lineNotifyToken"), lineSection.getString("url"))
  lazy val appConfig: AppConfig = AppConfig(appSection.getString("apiKey"))
}

case class LineConfig(lineNotifyToken: String, url: String)
case class AppConfig(apiKey: String)
