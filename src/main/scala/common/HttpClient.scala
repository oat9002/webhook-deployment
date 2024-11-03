package common

import cats.effect.{IO, Resource}
import org.http4s.client.Client
import org.http4s.ember.client.EmberClientBuilder

object HttpClient {
  val get: Resource[IO, Client[IO]] = EmberClientBuilder.default[IO].build
}
