package controllers

import cats.data.{Kleisli, OptionT}
import cats.effect.IO
import common.Configuration
import org.http4s._
import org.http4s.server.AuthMiddleware
import org.typelevel.ci.CIStringSyntax

object AuthenticationMiddleware {
  private val validateApiKey
      : Kleisli[IO, Request[IO], Either[String, Boolean]] = Kleisli { request =>
    val isValid = request.headers
      .get(ci"X-API-KEY")
      .map(_.head.value == Configuration.appConfig.apiKey)

    isValid match {
      case Some(true) => IO(Right(true))
      case _          => IO(Left("Unauthorized!"))
    }
  }

  private val onFailure: AuthedRoutes[String, IO] =
    Kleisli(req => OptionT.pure(Response[IO](Status.Unauthorized)))
  val apply: AuthMiddleware[IO, Boolean] =
    AuthMiddleware(validateApiKey, onFailure)
}
