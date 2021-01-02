import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{ContentTypes, HttpEntity}
import akka.http.scaladsl.server.Directives.{complete, concat, get, getFromResource, getFromResourceDirectory, path, pathPrefix, pathSingleSlash}
import controllers.WebHookRoute

import scala.concurrent.ExecutionContextExecutor

object Boot extends App {
  implicit val system: ActorSystem = ActorSystem()
  // needed for the future flatMap/onComplete in the end
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher
  val webHookRoute = WebHookRoute.apply

  val route =
    concat(
      pathSingleSlash {
        get {
          complete(
            HttpEntity(ContentTypes.`application/json`,
              "Say hello to akka-http"))
        }
      },
      pathPrefix("webhook")(webHookRoute.routes)
    )

  val combineRoutes = route
  val bindingFuture =
    Http().newServerAt("localhost", 8080).bind(combineRoutes)

  println(s"Server online at http://localhost:8080/")
}
