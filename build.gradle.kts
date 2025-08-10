plugins {
    scala
    application
}

repositories {
    mavenCentral()
}

group = "oat9002"
version = "1.30.0-SNAPSHOT"

application {
    mainClass = "Boot"
}

scala {
    scalaVersion = "2.13.16"
}

dependencies {
    implementation("org.http4s:http4s-ember-client_2.13:0.23.29")
    implementation("org.http4s:http4s-ember-server_2.13:0.23.29")
    implementation("org.http4s:http4s-dsl_2.13:0.23.29")
    implementation("org.http4s:http4s-circe_2.13:0.23.29")
    implementation("io.circe:circe-generic_2.13:0.14.9")
    implementation("com.typesafe:config:1.4.3")
    implementation("com.typesafe.scala-logging:scala-logging_2.13:3.9.4")
    implementation("ch.qos.logback:logback-classic:1.5.13")
    implementation("com.google.firebase:firebase-admin:9.5.0")
    testImplementation("org.scalatest:scalatest_2.13:3.2.17")
}

tasks.withType<ScalaCompile> {
    scalaCompileOptions.additionalParameters = listOf("-deprecation", "-feature")
}

tasks.named<ProcessResources>("processResources") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
