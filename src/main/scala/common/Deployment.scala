package common

import com.google.cloud.Timestamp
import common.Constants.ServiceEnum
import common.Constants.ServiceEnum.ServiceEnum

import java.time.LocalDateTime

case class Deployment(
    service: ServiceEnum,
    createdAt: Timestamp,
    isSuccess: Boolean
)

object Deployment {
  def apply(doc: java.util.Map[String, Object]): Deployment = {
    val serviceId = doc.get(Constants.serviceId).asInstanceOf[Long].toInt
    val createdAt = doc
      .get(Constants.createdAt)
      .asInstanceOf[Timestamp]
    val isSuccess =
      doc.get(Constants.isSuccess).asInstanceOf[Boolean]

    Deployment(ServiceEnum.fromInt(serviceId), createdAt, isSuccess)
  }
}
