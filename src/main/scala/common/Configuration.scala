package common

import com.typesafe.config.{Config, ConfigFactory}

object Configuration {
  private val conf: Config = ConfigFactory.load()
  private val lineSection = conf.getConfig("line")
  val lineConfig: LineConfig = LineConfig(lineSection.getString("lineNotifyToken"), lineSection.getString("url"))
}

case class LineConfig(lineNotifyToken: String, url: String)
