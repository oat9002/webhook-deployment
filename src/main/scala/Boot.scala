import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives.{complete, concat, get, getFromResource, getFromResourceDirectory, path, pathPrefix, pathSingleSlash, withRequestTimeout}
import common.Configuration
import controllers.{AuthenticationRoute, WebHookRoute}

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration.DurationInt
import cats.effect._
import cats.effect.unsafe.implicits.global
import cats.syntax.all._
import com.comcast.ip4s.{IpLiteralSyntax, Port}
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import org.http4s.server.Router

object Boot extends App {
  implicit val system: ActorSystem = ActorSystem()
  // needed for the future flatMap/onComplete in the end
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  val webHookRoute = WebHookRoute(executionContext, system)

//  val route =
//    concat(
//      pathSingleSlash {
//        get {
//          complete(
//            HttpEntity(ContentTypes.`application/json`,
//              "Say hello to akka-http"))
//        }
//      },
//      pathPrefix("webhook") {
//        withRequestTimeout(10.minutes)(webHookRoute.routes)
//      }
//    )
  val helloRoute = HttpRoutes.of[IO] {
    case GET -> Root =>
      Ok("Hello world!")
  }

  val route = helloRoute
  val httpApp = Router("/" -> route).orNotFound
  val port = Port.fromInt(Configuration.appConfig.port).getOrElse(port"8080")
  val server = EmberServerBuilder
    .default[IO]
    .withHost(ipv4"0.0.0.0")
    .withPort(port)
    .withHttpApp(httpApp)
    .build

  server.allocated.unsafeRunSync()._2

  println(s"Server online at http://localhost:${Configuration.appConfig.port}/")
}
