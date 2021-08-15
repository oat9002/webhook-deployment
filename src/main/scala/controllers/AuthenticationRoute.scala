package controllers

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Directive, Route, StandardRoute}
import common.Configuration

class AuthenticationRoute {
  val routes: Route = {
    optionalHeaderValueByName("X-Api-Key") {
      case Some(apiKey) if validate(apiKey) => reject
      case _ => complete(StatusCodes.Unauthorized)
    }
  }

  private def validate(apiKey: String): Boolean = {
    apiKey.equals(Configuration.appConfig.apiKey)
  }
}

object AuthenticationRoute {
  def apply(): AuthenticationRoute = new AuthenticationRoute()
}
