package models.requests

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

case class LineNotifyRequest(message: String)

trait LineNotifyRequestJsonProtocol extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val lineNotifyRequestJsonFormat: RootJsonFormat[LineNotifyRequest] = jsonFormat1(LineNotifyRequest)
}
