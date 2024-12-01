package controllers

import cats.data.Kleisli
import cats.effect.IO
import cats.effect.implicits.genTemporalOps
import org.http4s.{HttpRoutes, Request}

import scala.concurrent.duration.FiniteDuration

object RequestTimeoutMiddleware {
  def apply(timeout: FiniteDuration)(httpRoutes: HttpRoutes[IO]): HttpRoutes[IO] = {
    Kleisli { req: Request[IO] => httpRoutes(req).timeout(timeout)
    }
  }
}
