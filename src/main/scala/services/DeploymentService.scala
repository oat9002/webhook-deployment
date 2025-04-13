package services

import cats.effect.IO

trait DeploymentService {
  protected val telegramService: TelegramService

  def deployWithNotifyMessage(
      prefixMessage: String => String,
      deployFn: () => Boolean
  ): IO[Boolean] = {
    telegramService
      .notify(prefixMessage("Start Deployment"))
      .flatMap { isSuccess =>
        if (isSuccess) {
          val isError = deployFn()

          if (isError) {
            telegramService.notify(
              prefixMessage("Deployment is failed")
            )
          } else {
            telegramService
              .notify(prefixMessage("Deployment is complete"))
          }
        } else {
          telegramService.notify(
            prefixMessage("Deployment is failed")
          )
        }
      }
  }

}
