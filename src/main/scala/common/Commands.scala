package common

object Commands {
  val goldPriceTrackingDeploy = List(
    "docker stop server_gold-price-tracking-server_1",
    "docker rm server_gold-price-tracking-server_1",
    "docker image rm oat9002/gold-price-tracking-server",
    "docker-compose -f /root/dev/GoldPriceTracking/server/docker-compose.yml up -d")
}
