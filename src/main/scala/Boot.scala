import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives.{complete, concat, get, getFromResource, getFromResourceDirectory, path, pathPrefix, pathSingleSlash, withRequestTimeout}
import common.Configuration
import controllers.{AuthenticationRoute, WebHookRoute}

import scala.concurrent.ExecutionContextExecutor
import scala.concurrent.duration.DurationInt

object Boot extends App {
  implicit val system: ActorSystem = ActorSystem()
  // needed for the future flatMap/onComplete in the end
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  val webHookRoute = WebHookRoute(executionContext, system)

  val route =
    concat(
      pathSingleSlash {
        get {
          complete(
            HttpEntity(ContentTypes.`application/json`,
              "Say hello to akka-http"))
        }
      },
      pathPrefix("webhook") {
        withRequestTimeout(10.minutes)(webHookRoute.routes)
      }
    )

  val bindingFuture =
    Http().newServerAt("localhost", Configuration.appConfig.port).bind(route)

  println(s"Server online at http://localhost:${Configuration.appConfig.port}/")
}
