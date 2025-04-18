import cats.effect._
import cats.implicits.toSemigroupKOps
import com.comcast.ip4s.{IpLiteralSyntax, Port}
import com.typesafe.scalalogging.LazyLogging
import common.{Configuration, EnvironmentHelper}
import controllers.{TestRoute, WebHookRoute}
import org.http4s._
import org.http4s.dsl.io._
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits._
import org.http4s.server.Router
import services.{
  CryptoNotifyService,
  FirebaseService,
  GoldPriceTrackingService,
  TelegramService
}

object Boot extends IOApp with LazyLogging {
  private val helloRoute = HttpRoutes.of[IO] { case GET -> Root =>
    Ok("Hello world!")
  }
  private val telegramService: TelegramService = TelegramService()
  private val cryptoNotifyService: CryptoNotifyService =
    CryptoNotifyService(telegramService)
  private val goldPriceTrackingService: GoldPriceTrackingService =
    GoldPriceTrackingService(telegramService)
  private val firebaseService: FirebaseService =
    FirebaseService(goldPriceTrackingService, cryptoNotifyService)
  private val webHookRoute =
    WebHookRoute(telegramService, goldPriceTrackingService).route
  private val testRoute = TestRoute().route
  private val route = helloRoute <+> webHookRoute <+> testRoute
  private val httpApp = Router("/" -> route).orNotFound
  private val port =
    Port.fromInt(Configuration.appConfig.port).getOrElse(port"8080")

  firebaseService.subscribeToDeployment()

  override def run(args: List[String]): IO[ExitCode] = {
    val app = EmberServerBuilder
      .default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port)
      .withHttpApp(httpApp)
      .build
      .use(_ => IO.never)
      .as(ExitCode.Success)

    logger.info(
      s"Server online at http://localhost:${Configuration.appConfig.port}/"
    )
    logger.info(
      s"Environment: ${if (EnvironmentHelper.isDevelopment) "Development"
      else "Production"}"
    )

    app
  }
}
