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
import common.{Configuration, Constants, Deployment}

import java.io.ByteArrayInputStream

trait FirebaseService {
  def subscribeToDeployment(): IO[Unit]
}

class FirebaseServiceImpl(
    goldPriceTrackingService: GoldPriceTrackingService
) extends FirebaseService {

  override def subscribeToDeployment(): IO[Unit] = {
    val firestore: Firestore = Firebase.firestore
    val collectionRef = firestore.collection(Constants.deployments)

    IO.pure(
      collectionRef.addSnapshotListener(
        (value, _) => {
          if (value != null && !value.isEmpty) {
            value.getDocumentChanges.forEach { change =>
              if (change.getType == DocumentChange.Type.ADDED) {
                val doc = change.getDocument
                val data = doc.getData
                val deploymentService =
                  Deployment(data)

                deploymentService.service match {
                  case Constants.ServiceEnum.GoldPriceTracking
                      if !deploymentService.isSuccess =>
                    goldPriceTrackingService
                      .deploy()
                      .flatMap { isSuccess =>
                        if (isSuccess) {
                          updateDeploymentComplete(doc.getId)
                        } else {
                          IO.println("Deployment failed")
                        }
                      }
                }
              }
            }
          } else {
            println("No documents found in the collection.")
          }
        }
      )
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
      goldPriceTrackingService: GoldPriceTrackingService
  ): FirebaseService = {
    new FirebaseServiceImpl(goldPriceTrackingService)
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
