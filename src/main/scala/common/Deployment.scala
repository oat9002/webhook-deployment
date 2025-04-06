package common

import common.Constants.ServiceEnum
import common.Constants.ServiceEnum.ServiceEnum

import java.time.LocalDateTime

case class Deployment(
    service: ServiceEnum,
    createdAt: LocalDateTime,
    isSuccess: Boolean
)

object Deployment {
  def apply(doc: Map[String, AnyRef]): Deployment = {
    val serviceId = doc.getOrElse(Constants.serviceId, 0).asInstanceOf[Int]
    val createdAt = doc
      .getOrElse(Constants.createdAt, LocalDateTime.now())
      .asInstanceOf[LocalDateTime]
    val isSuccess =
      doc.getOrElse(Constants.isSuccess, false).asInstanceOf[Boolean]

    Deployment(ServiceEnum.fromInt(serviceId), createdAt, isSuccess)
  }
}
