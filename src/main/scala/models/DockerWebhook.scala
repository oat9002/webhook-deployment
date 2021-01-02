package models

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

case class DockerWebhook(callbackUrl: String,
                         pushData: PushData,
                         repository: Repository
                        )

trait DockerWebhookJsonProtocol extends PushDataJsonProtocol with RepositoryJsonProtocol {
  implicit val dockerWebHookJsonFormat: RootJsonFormat[DockerWebhook] = jsonFormat(DockerWebhook, "callback_url", "push_data", "repository")
}

case class PushData(images: List[String],
                    pushedAt: BigInt,
                    pusher: String,
                    tag: String)

trait PushDataJsonProtocol extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val pushDataJsonFormat: RootJsonFormat[PushData] = jsonFormat(PushData, "images", "pushed_at", "pusher", "tag")
}

case class Repository(commentCount: Int,
                      dateCreated: BigInt,
                      description: String,
                      dockerfile: String,
                      fullDescription: String,
                      isOfficial: Boolean,
                      isPrivate: Boolean,
                      isTrusted: Boolean,
                      name: String,
                      namespace: String,
                      owner: String,
                      repoName: String,
                      repoUrl: String,
                      starCount: Int,
                      status: String
                     )

trait RepositoryJsonProtocol extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val repositoryJsonFormat: RootJsonFormat[Repository] = jsonFormat(Repository,
    "comment_count",
    "date_created",
    "description",
    "dockerfile",
    "full_description",
    "is_official",
    "is_private",
    "is_trusted",
    "name",
    "namespace",
    "owner",
    "repo_name",
    "repo_url",
    "star_count",
    "status")
}
