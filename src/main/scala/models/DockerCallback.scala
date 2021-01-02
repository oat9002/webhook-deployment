package models

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

case class DockerCallback(state: String, description: String, context: String, targetUrl: String)

trait DockerCallbackJsonProtocol extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val dockerCallbackJsonFormat: RootJsonFormat[DockerCallback] = jsonFormat(DockerCallback, "state", "description", "context", "target_url")
}

