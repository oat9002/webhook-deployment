package services

import cats.effect.IO
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.firestore.{
  DocumentChange,
  EventListener,
  Firestore,
  FirestoreException,
  FirestoreOptions,
  ListenerRegistration,
  QuerySnapshot
}
import com.google.firebase.cloud.FirestoreClient
import com.google.firebase.{FirebaseApp, FirebaseOptions}
import com.typesafe.scalalogging.LazyLogging
import common.{Configuration, Constants, Deployment}

import java.io.ByteArrayInputStream

trait FirebaseService {
  def subscribeToDeployment(): IO[Unit]
}

class FirebaseServiceImpl(
    goldPriceTrackingService: GoldPriceTrackingService,
    cryptoNotifyService: CryptoNotifyService
) extends FirebaseService
    with LazyLogging {

  override def subscribeToDeployment(): IO[Unit] = {
    val firestore: Firestore = Firebase.firestore
    val collectionRef = firestore.collection(Constants.deployments)

    IO.pure(
      collectionRef.addSnapshotListener((value, _) => {
        logger.info("Listening to Firestore changes")

        if (value != null && !value.isEmpty) {
          value.getDocumentChanges.forEach { change =>
            if (change.getType == DocumentChange.Type.ADDED) {
              val doc = change.getDocument
              val data = doc.getData
              val deploymentService =
                Deployment(data)

              deploymentService.service match {
                case Constants.ServiceEnum.CryptoNotify
                    if !deploymentService.isSuccess =>
                  logger.info("Deploying CryptoNotifyService")
                  cryptoNotifyService.deploy().flatMap { isSuccess =>
                    if (isSuccess) {
                      updateDeploymentComplete(doc.getId)
                    } else {
                      logger.error("Deployment failed")
                      IO.unit
                    }
                  }
                case Constants.ServiceEnum.GoldPriceTracking
                    if !deploymentService.isSuccess =>
                  logger.info("Deploying GoldPriceTrackingService")
                  goldPriceTrackingService
                    .deploy()
                    .flatMap { isSuccess =>
                      if (isSuccess) {
                        updateDeploymentComplete(doc.getId)
                      } else {
                        logger.error("Deployment failed")
                        IO.unit
                      }
                    }
              }
            }
          }
        } else {
          logger.info("No documents found in the collection.")
        }
      })
    )
  }

  private def updateDeploymentComplete(docId: String): IO[Unit] = {
    val firestore: Firestore = Firebase.firestore
    val collectionRef = firestore.collection(Constants.deployments)
    val docRef = collectionRef.document(docId)

    IO.pure(docRef.update(Constants.isSuccess, true).get())
  }
}

object FirebaseService {
  def apply(
      goldPriceTrackingService: GoldPriceTrackingService,
      cryptoNotifyService: CryptoNotifyService
  ): FirebaseService = {
    new FirebaseServiceImpl(goldPriceTrackingService, cryptoNotifyService)
  }
}

private object Firebase {
  private val secretAccount = Configuration.firebase.secret
  private val firebaseOptions = FirebaseOptions
    .builder()
    .setCredentials(
      GoogleCredentials.fromStream(
        new ByteArrayInputStream(secretAccount.getBytes("UTF-8"))
      )
    )
    .build()

  FirebaseApp.initializeApp(firebaseOptions)

  val firestore: Firestore = FirestoreClient.getFirestore
}
