import cats.effect._
import cats.implicits.toSemigroupKOps
import com.comcast.ip4s.{IpLiteralSyntax, Port}
import common.Configuration
import controllers.{AuthenticationMiddleware, WebHookRoute}
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import org.http4s.server.Router

object Boot extends IOApp {
  private val helloRoute = HttpRoutes.of[IO] {
    case GET -> Root =>
      Ok("Hello world!")
  }

  private val webHookRoute = WebHookRoute().route

  private val route = helloRoute <+> webHookRoute
  private val httpApp = Router("/" -> route ).orNotFound
  private val port = Port.fromInt(Configuration.appConfig.port).getOrElse(port"8080")

  override def run(args: List[String]): IO[ExitCode] = {
    val app = EmberServerBuilder
      .default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port)
      .withHttpApp(httpApp)
      .build
      .use(_ => IO.never)
      .as(ExitCode.Success)

    println(s"Server online at http://localhost:${Configuration.appConfig.port}/")

    app
  }
}
