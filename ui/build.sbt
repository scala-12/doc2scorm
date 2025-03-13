import scalariform.formatter.preferences._

name := "ilogos-course-generator-ui"

version := "1.0-BETA"

scalaVersion := "2.11.7"

val akkaVersion = "2.4.6"

resolvers := ("Atlassian Releases" at "https://maven.atlassian.com/public/") +: resolvers.value

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"

resolvers += Resolver.sonatypeRepo("snapshots")

libraryDependencies ++= Seq(
  "com.mohiva" %% "play-silhouette" % "3.0.4",
  "org.webjars" %% "webjars-play" % "2.4.0-1",
  "net.codingwell" %% "scala-guice" % "4.0.0",
  "net.ceedubs" %% "ficus" % "1.1.2",
  "com.adrianhurt" %% "play-bootstrap3" % "0.4.4-P24",
  "com.mohiva" %% "play-silhouette-testkit" % "3.0.4" % "test",
  specs2 % Test,
  cache,
  filters
)

lazy val root = (project in file(".")).enablePlugins(PlayScala)

routesGenerator := InjectedRoutesGenerator

scalacOptions ++= Seq(
  "-deprecation", // Emit warning and location for usages of deprecated APIs.
  "-feature", // Emit warning and location for usages of features that should be imported explicitly.
  "-unchecked", // Enable additional warnings where generated code depends on assumptions.
  "-Xfatal-warnings", // Fail the compilation if there are any warnings.
  "-Xlint", // Enable recommended additional warnings.
  "-Ywarn-adapted-args", // Warn if an argument list is modified to match the receiver.
  "-Ywarn-dead-code", // Warn when dead code is identified.
  "-Ywarn-inaccessible", // Warn about inaccessible types in method signatures.
  "-Ywarn-nullary-override", // Warn when non-nullary overrides nullary, e.g. def foo() over def foo.
  "-Ywarn-numeric-widen" // Warn when numerics are widened.
)

//********************************************************
// Scalariform settings
//********************************************************

defaultScalariformSettings

ScalariformKeys.preferences := ScalariformKeys.preferences.value
  .setPreference(FormatXml, false)
  .setPreference(DoubleIndentClassDeclaration, false)
  .setPreference(PreserveDanglingCloseParenthesis, true)

// angular2
incOptions := incOptions.value.withNameHashing(true)
updateOptions := updateOptions.value.withCachedResolution(cachedResoluton = true)

libraryDependencies ++= Seq(
  //angular2 dependencies
  "org.webjars.npm" % "angular2" % "2.0.0-beta.14",
  "org.webjars.npm" % "systemjs" % "0.19.24",
  "org.webjars.npm" % "rxjs" % "5.0.0-beta.2",
  "org.webjars.npm" % "es6-promise" % "3.0.2",
  "org.webjars.npm" % "es6-shim" % "0.35.0",
  "org.webjars.npm" % "reflect-metadata" % "0.1.2",
  "org.webjars.npm" % "zone.js" % "0.6.4",
  "org.webjars.npm" % "typescript" % "1.8.7",

  "org.webjars.npm" % "bootstrap" % "3.3.6",

  //tslint dependency
  "org.webjars.npm" % "tslint-eslint-rules" % "1.0.1"
)
dependencyOverrides += "org.webjars.npm" % "minimatch" % "2.0.10"


// the typescript typing information is by convention in the typings directory
// It provides ES6 implementations. This is required when compiling to ES5.
typingsFile := Some(baseDirectory.value / "typings" / "browser.d.ts")

// use the webjars npm directory (target/web/node_modules ) for resolution of module imports of angular2/core etc
resolveFromWebjarsNodeModulesDir := true

// use the combined tslint and eslint rules
(rulesDirectories in tslint) := Some(List((tslintEslintRulesDir).value))

routesGenerator := InjectedRoutesGenerator

// Typesafe Slick
libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-slick" % "1.1.1",
  "com.typesafe.play" %% "play-slick-evolutions" % "1.1.1",
  "org.slf4j" % "slf4j-nop" % "1.6.4",
  "mysql" % "mysql-connector-java" % "5.1.16"
)

libraryDependencies += evolutions

// Testing
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.0" % "test"

libraryDependencies += "org.webjars.npm" % "jasmine-core" % "2.4.1"

resolvers += Resolver.mavenLocal

// Course generator core
libraryDependencies += "com.ipoint" % "ilogos-course-generator-core" % "0.0.1-SNAPSHOT"

// akka libararies
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion
  , "com.typesafe.akka" %% "akka-cluster" % akkaVersion
)

// Ignore files of configs
mappings in Universal := {
  val origMappings = (mappings in Universal).value
  origMappings.filterNot { case (_, file) => file.endsWith("local.conf") }
}

// for work with json
libraryDependencies += "com.google.code.gson" % "gson" % "2.8.2"