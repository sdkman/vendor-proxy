enablePlugins(JavaServerAppPackaging)

enablePlugins(DockerPlugin)

name := """vendor-proxy"""

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.10"

routesGenerator := InjectedRoutesGenerator

libraryDependencies ++= Seq(
  ws,
  guice,
  "com.typesafe.play" %% "play-json" % "2.8.1",
  "org.postgresql" % "postgresql" % "42.5.4",
  "com.typesafe.play" %% "play-slick" % "4.0.2",
  "org.flywaydb" %% "flyway-play" % "5.4.0",
  "org.scalatest" %% "scalatest" % "3.1.0" % Test,
  "io.cucumber" %% "cucumber-scala" % "4.7.1" % Test,
  "io.cucumber" % "cucumber-junit" % "4.7.1" % Test,
  "info.cukes" % "gherkin" % "2.7.3" % Test,
  "org.scalaj" %% "scalaj-http" % "2.4.0" % Test,
  "com.github.tomakehurst" % "wiremock-jre8-standalone" % "2.29.1" % Test
)

unmanagedResourceDirectories in Test += baseDirectory.value / "features"

dockerBaseImage := "adoptopenjdk/openjdk11:jdk-11.0.10_9-debian-slim"
maintainer in Docker := "Marco Vermeulen <marco@sdkman.io>"
dockerUpdateLatest := true
packageName := "sdkman/vendor-proxy"
dockerExposedPorts ++= Seq(9000)

javaOptions in Universal ++= Seq(
  "-J-Xms128m -J-Xmx224m -Dpidfile.path=/dev/null"
)

import ReleaseTransformations._
releaseProcess := Seq[ReleaseStep](
  checkSnapshotDependencies,
  inquireVersions,
  runClean,
  runTest,
  setReleaseVersion,
  commitReleaseVersion,
  tagRelease,
  ReleaseStep(releaseStepTask(publish in Docker)),
  setNextVersion,
  commitNextVersion,
  pushChanges
)
