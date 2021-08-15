package common

import com.typesafe.config.{Config, ConfigFactory}

object Configuration {
  private val conf: Config = {
    val c = ConfigFactory.load()
    val env = c.getConfig("app").getString("env").toLowerCase.trim
    if (!"production".equals(env)) {
      val toReturn = ConfigFactory.load("application.local")
      ConfigFactory.invalidateCaches()
      toReturn
    } else {
      c
    }
  }
  private val lineSection = conf.getConfig("line")
  private val appSection = conf.getConfig("app")
  lazy val lineConfig: LineConfig = LineConfig(lineSection.getString("lineNotifyToken"), lineSection.getString("url"))
  lazy val appConfig: AppConfig = AppConfig(appSection.getString("env"), appSection.getString("apiKey"), appSection.getInt("port"))
}

case class LineConfig(lineNotifyToken: String, url: String)
case class AppConfig(env: String, apiKey: String, port: Int)
