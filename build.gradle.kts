plugins {
    scala
    application
    id("net.researchgate.release") version "3.0.2"
}

repositories {
    mavenCentral()
}

group = "oat9002"
version = "1.30.0-SNAPSHOT"

application {
    mainClass.set("main.Boot")
}

dependencies {
    implementation("org.http4s:http4s-ember-client_2.13:0.23.29")
    implementation("org.http4s:http4s-ember-server_2.13:0.23.29")
    implementation("org.http4s:http4s-dsl_2.13:0.23.29")
    implementation("org.http4s:http4s-circe_2.13:0.23.29")
    implementation("io.circe:circe-generic_2.13:0.14.9")
    implementation("com.typesafe:config:1.4.3")
    implementation("com.typesafe.scala-logging:scala-logging_2.13:3.9.4")
    implementation("ch.qos.logback:logback-classic:1.5.12")
    implementation("com.google.firebase:firebase-admin:9.4.3")
    testImplementation("org.scalatest:scalatest_2.13:3.2.17")
}

// Scala compile options
 tasks.withType<ScalaCompile> {
    scalaCompileOptions.additionalParameters = listOf("-deprecation", "-feature")
}

// Source sets
sourceSets {
    main {
        scala.srcDirs("src/main/scala")
        resources.srcDirs("src/main/resources")
    }
    test {
        scala.srcDirs("src/test/scala")
    }
}

tasks.named<ProcessResources>("processResources") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
