import dependencies.versions._

name := "webhook-deployment"
maintainer := "oat9002"

scalaVersion := "2.13.4"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http" % akkaHttp,
  "com.typesafe.akka" %% "akka-stream" % akkaStream,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpSprayJson,
  "com.typesafe.slick" %% "slick" % slick,
  "org.slf4j" % "slf4j-nop" % slf4,
  "com.typesafe" % "config" % conf,
)

enablePlugins(JavaServerAppPackaging)

releaseVersionBump := sbtrelease.Version.Bump.Minor
releaseUseGlobalVersion := false
releaseIgnoreUntrackedFiles := true

