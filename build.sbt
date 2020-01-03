import com.typesafe.config._

enablePlugins(JavaServerAppPackaging)

enablePlugins(DockerPlugin)

val conf = ConfigFactory.parseFile(new File("conf/application.conf")).resolve()

version := conf.getString("application.version")

name := """vendor-proxy"""

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.10"

routesGenerator := InjectedRoutesGenerator

libraryDependencies ++= Seq(
  ws,
  guice,
  "com.typesafe.play" %% "play-json" % "2.8.1",
  "org.postgresql" % "postgresql" % "9.4.1211",
  "com.typesafe.play" %% "play-slick" % "4.0.2",
  "org.flywaydb" %% "flyway-play" % "5.3.2",
  "org.scalatest" %% "scalatest" % "3.1.0" % Test,
  "io.cucumber" %% "cucumber-scala" % "4.7.1" % Test,
  "io.cucumber" % "cucumber-junit" % "4.7.1" % Test,
  "info.cukes" % "gherkin" % "2.7.3" % Test,
  "org.scalaj" %% "scalaj-http" % "2.4.0" % Test,
  "com.github.tomakehurst" % "wiremock" % "2.2.2" % Test
)

unmanagedResourceDirectories in Test += baseDirectory.value / "features"

packageName in Docker := "sdkman/vendor-proxy"
dockerBaseImage := "openjdk:11"
dockerExposedPorts ++= Seq(9000)

javaOptions in Universal ++= Seq(
  "-Dpidfile.path=/dev/null"
)
