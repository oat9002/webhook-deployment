package services

import cats.effect.IO
import cats.effect.unsafe.implicits.global
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
  def subscribeToDeployment(): Unit
}

class FirebaseServiceImpl(
    goldPriceTrackingService: GoldPriceTrackingService,
    cryptoNotifyService: CryptoNotifyService
) extends FirebaseService
    with LazyLogging {

  override def subscribeToDeployment(): Unit = {
    val firestore: Firestore = Firebase.firestore
    val collectionRef = firestore.collection(Constants.deployments)

    collectionRef.addSnapshotListener((value, _) => {
      if (value != null && !value.isEmpty) {
        val change = value.getDocumentChanges.getLast

        if (change.getType == DocumentChange.Type.ADDED) {
          val doc = change.getDocument
          val data = doc.getData
          val deploymentService =
            Deployment(data)

          val isSuccess = deploymentService.service match {
            case Constants.ServiceEnum.CryptoNotify
                if !deploymentService.isSuccess =>
              cryptoNotifyService.deploy()

            case Constants.ServiceEnum.GoldPriceTracking
                if !deploymentService.isSuccess =>
              goldPriceTrackingService
                .deploy()
          }

          if (isSuccess.unsafeRunSync()) {
            updateDeploymentComplete(doc.getId).unsafeRunSync()
          } else {
            logger.error(
              s"Failed to deploy ${deploymentService.service} service."
            )
          }
        }
      } else {
        logger.info("No documents found in the collection.")
      }
    })
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
