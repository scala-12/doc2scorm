import scalariform.formatter.preferences._

name := "ilogos-course-generator-ui"

version := "1.0-BETA"

scalaVersion := "2.11.7"

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
  "org.webjars.npm" % "angular2" % "2.0.0-beta.11",
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

// course-generator-core
libraryDependencies ++= Seq(
  "org.apache.xmlbeans" % "xmlbeans" % "2.5.0",
  "org.apache.commons" % "commons-io" % "1.3.2",
  "org.apache.poi" % "poi-ooxml" % "3.11",
  "org.apache.poi" % "poi-ooxml-schemas" % "3.11",
  "org.apache.poi" % "poi-scratchpad" % "3.11",
  "org.apache.poi" % "ooxml-schemas" % "1.1",
  "org.htmlparser" % "htmlparser" % "2.1",
  "org.freemarker" % "freemarker" % "2.3.22",
  "org.jdom" % "jdom" % "1.1",
  "org.freehep" % "freehep-graphicsio-emf" % "2.4",
  "org.freehep" % "freehep-graphicsio-svg" % "2.4",
  "net.arnx" % "wmf2svg" % "0.9.0",
  "org.apache.xmlgraphics" % "batik-transcoder" % "1.8",
  "org.apache.xmlgraphics" % "batik-codec" % "1.8",
  "org.apache.commons" % "commons-lang3" % "3.4",
  "org.docx4j" % "docx4j" % "3.2.1",
  "com.google.collections" % "google-collections" % "1.0-rc2"
)

// Typesafe Slick
libraryDependencies ++= Seq(
	"com.typesafe.play" %% "play-slick" % "1.1.1",
	"com.typesafe.play" %% "play-slick-evolutions" % "1.1.1",
	"org.slf4j" % "slf4j-nop" % "1.6.4",
	"mysql" % "mysql-connector-java" % "5.1.16"
)

libraryDependencies += evolutions