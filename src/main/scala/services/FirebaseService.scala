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
import com.google.firebase.{FirebaseApp, FirebaseOptions}
import common.{Constants, Deployment}

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
        (value: QuerySnapshot, error: FirestoreException) => {
          if (error != null) {
            println(s"Error listening to Firestore: ${error.getMessage}")

            IO.canceled
          } else if (value != null && !value.isEmpty) {
            value.getDocumentChanges.forEach { change =>
              if (change.getType == DocumentChange.Type.ADDED) {
                val doc = change.getDocument
                val data = doc.getData
                val deploymentService =
                  Deployment(data.asInstanceOf[Map[String, AnyRef]])

                deploymentService.service match {
                  case Constants.ServiceEnum.CryptoNotify =>
                    IO.canceled
                  case Constants.ServiceEnum.GoldPriceTracking =>
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
            IO.println("No changes in Firestore")
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
  val app: FirebaseApp = {
    val secretAccount =
      System.getenv("DEPLOYMENT_TRIGGERER_FIREBASE_SERVICE_ACCOUNT_KEY_JSON")

    if (secretAccount.isEmpty) {
      throw new IllegalArgumentException(
        "Firebase service account key JSON is not set in environment variable DEPLOYMENT_TRIGGERER_FIREBASE_SERVICE_ACCOUNT_KEY_JSON"
      )
    }

    val firebaseOptions = FirebaseOptions
      .builder()
      .setCredentials(
        GoogleCredentials.fromStream(
          new ByteArrayInputStream(secretAccount.getBytes("UTF-8"))
        )
      )
      .build()

    FirebaseApp.initializeApp(firebaseOptions)
  }

  val firestore: Firestore = FirestoreOptions.getDefaultInstance.getService
}
