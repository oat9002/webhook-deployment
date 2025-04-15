package services

import cats.effect.IO
import com.typesafe.scalalogging.LazyLogging

trait DeploymentService extends LazyLogging {
  protected val telegramService: TelegramService
  private val startDeployment = "Start Deployment"
  private val deploymentFailed = "Deployment is failed"
  private val deploymentComplete = "Deployment is complete"

  def deployWithNotifyMessage(
      prefixMessage: String => String,
      deployFn: () => Boolean
  ): IO[Boolean] = {
    logger.info(prefixMessage(startDeployment))
    telegramService
      .notify(prefixMessage(startDeployment))
      .flatMap { isSuccess =>
        if (isSuccess) {
          val isError = deployFn()

          if (isError) {
            logger.info(prefixMessage(deploymentFailed))
            telegramService.notify(
              prefixMessage(deploymentFailed)
            )
          } else {
            logger.info(prefixMessage(deploymentComplete))
            telegramService
              .notify(prefixMessage(deploymentComplete))
          }
        } else {
          logger.info(prefixMessage(deploymentFailed))
          telegramService.notify(
            prefixMessage(deploymentFailed)
          )
        }
      }
  }

}
