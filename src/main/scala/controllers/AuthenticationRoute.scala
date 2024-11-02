package controllers

import cats.data.{Kleisli, OptionT}
import cats.effect.{ExitCode, IO, IOApp}
import common.Configuration
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.server.AuthMiddleware
import org.typelevel.ci.CIStringSyntax

class AuthenticationRoute {
  private val validateApiKey: Kleisli[IO, Request[IO], Either[String, Boolean]] = Kleisli { request =>
    val isValid = request.headers.get(ci"X-API-KEY").map(_.head.value == Configuration.appConfig.apiKey)

    isValid match {
      case Some(true) => IO.pure(Right(true))
      case _ => IO.pure(Left("Unauthorized"))
    }
  }

  val onFailure: AuthedRoutes[String, IO] = Kleisli(req => OptionT.pure(Response[IO](Status.Unauthorized).withEntity(req.context)))
  val middleware: AuthMiddleware[IO, Boolean] = AuthMiddleware(validateApiKey, onFailure)
}

object AuthenticationRoute {
  def apply(): AuthenticationRoute = new AuthenticationRoute()
}
