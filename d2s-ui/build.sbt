name := """d2s-UI"""
organization := "i.point"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.3"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test

// Adds additional packages into Twirl
//TwirlKeys.templateImports += "i.point.controllers._"

// Adds additional packages into conf/routes
// play.sbt.routes.RoutesKeys.routesImport += "i.point.binders._"

// akka libraries
val akkaVersion = "2.5.6"
libraryDependencies += "com.typesafe.akka" % "akka-actor_2.12" % akkaVersion
libraryDependencies += "com.typesafe.akka" % "akka-cluster_2.12" % akkaVersion

// silhouette authorization library
libraryDependencies ++= Seq(
  "com.mohiva" % "play-silhouette_2.12" % "5.0.1",
  "com.mohiva" % "play-silhouette-password-bcrypt_2.12" % "5.0.1",
  "com.mohiva" % "play-silhouette-persistence_2.12" % "5.0.1",
  "com.mohiva" % "play-silhouette-crypto-jca_2.12" % "5.0.1",
  "org.webjars" %% "webjars-play" % "2.6.1",
  "org.webjars" % "bootstrap" % "3.3.7-1" exclude("org.webjars", "jquery"),
  "org.webjars" % "jquery" % "3.2.1",
  "net.codingwell" %% "scala-guice" % "4.1.0",
  "com.iheart" %% "ficus" % "1.4.1",
  "com.adrianhurt" %% "play-bootstrap" % "1.2-P26-B3",
  ehcache,
  ehcache,
  guice,
  filters
)