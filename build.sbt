import Dependency.*
import sbtrelease.ReleaseStateTransformations.{checkSnapshotDependencies, commitNextVersion, commitReleaseVersion, inquireVersions, pushChanges, runClean, runTest, setNextVersion, setReleaseVersion, tagRelease}

ThisBuild / scalaVersion := "2.13.15"

lazy val root = project
  .in(file("."))
  .settings(
    name := "webhook-deployment",
    maintainer := "oat9002",
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-ember-client" % http4s,
      "org.http4s" %% "http4s-ember-server" % http4s,
      "org.http4s" %% "http4s-dsl"          % http4s,
      "org.http4s" %% "http4s-circe" % http4s,
      "io.circe" %% "circe-generic" % circe,
      "com.typesafe" % "config" % conf,
      "com.typesafe.scala-logging" %% "scala-logging" % scalaLogging,
      "ch.qos.logback" % "logback-classic" % logback
    )
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

