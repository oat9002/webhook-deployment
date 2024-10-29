import Dependency.*
import sbtrelease.ReleaseStateTransformations.{checkSnapshotDependencies, commitNextVersion, commitReleaseVersion, inquireVersions, pushChanges, runClean, runTest, setNextVersion, setReleaseVersion, tagRelease}

name := "webhook-deployment"
maintainer := "oat9002"

scalaVersion := "2.13.15"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % akkaHttp,
  "com.typesafe.akka" %% "akka-stream" % akkaStream,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpSprayJson,
  "org.http4s" %% "http4s-ember-client" % http4s,
  "org.http4s" %% "http4s-ember-server" % http4s,
  "org.http4s" %% "http4s-dsl"          % http4s,
  "org.http4s" %% "http4s-circe" % http4s,
  "io.circe" %% "circe-generic" % circe,
  "com.typesafe.slick" %% "slick" % slick,
  "org.slf4j" % "slf4j-nop" % slf4,
  "com.typesafe" % "config" % conf,
)

enablePlugins(JavaServerAppPackaging)

releaseVersionBump := sbtrelease.Version.Bump.Minor
releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,              // : ReleaseStep
  inquireVersions,                        // : ReleaseStep
  runClean,                               // : ReleaseStep
  runTest,                                // : ReleaseStep
  setReleaseVersion,                      // : ReleaseStep
  commitReleaseVersion,                   // : ReleaseStep, performs the initial git checks
  tagRelease,                             // : ReleaseStep
  setNextVersion,                         // : ReleaseStep
  commitNextVersion,                      // : ReleaseStep
  pushChanges                             // : ReleaseStep, also checks that an upstream branch is properly configured
)
releaseUseGlobalVersion := false
releaseIgnoreUntrackedFiles := true

