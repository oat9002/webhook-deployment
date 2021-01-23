package models.requests

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

case class DockerCallbackRequest(state: String, description: String, context: String, targetUrl: String)

trait DockerCallbackJsonProtocol extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val dockerCallbackJsonFormat: RootJsonFormat[DockerCallbackRequest] = jsonFormat(DockerCallbackRequest, "state", "description", "context", "target_url")
}

