package common

object Constants {
  object ServiceEnum extends Enumeration {
    type ServiceEnum = Value
    val CryptoNotify: Value = Value(1, "crypto-notify")
    val GoldPriceTracking: Value = Value(2, "gold-price-tracking")

    def fromString(name: String): ServiceEnum = {
      name match {
        case "crypto-notify"       => CryptoNotify
        case "gold-price-tracking" => GoldPriceTracking
        case _                     => throw new IllegalArgumentException(s"Unknown service: $name")
      }
    }

    def fromLong(id: Long): ServiceEnum = {
      id match {
        case 1 => CryptoNotify
        case 2 => GoldPriceTracking
        case _ => throw new IllegalArgumentException(s"Unknown service ID: $id")
      }
    }
  }

  val serviceId = "service_id"
  val createdAt = "created_at"
  val isSuccess = "is_success"
  val deployments = "deployments"
}
