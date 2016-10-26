import com.typesafe.config._

enablePlugins(JavaServerAppPackaging)

enablePlugins(DockerPlugin)

val conf = ConfigFactory.parseFile(new File("conf/application.conf")).resolve()

version := conf.getString("application.version")

name := """vendor-proxy"""

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"

routesGenerator := InjectedRoutesGenerator

libraryDependencies ++= Seq(
  ws,
  "org.postgresql" % "postgresql" % "9.4.1211",
  "com.typesafe.play" %% "play-slick" % "2.0.0",
  "org.scalatest" %% "scalatest" % "3.0.0" % "test",
  "info.cukes" %% "cucumber-scala" % "1.2.5" % "test",
  "info.cukes" % "cucumber-junit" % "1.2.5" % "test",
  "org.scalaj" %% "scalaj-http" % "0.3.16" % "test",
  "com.github.tomakehurst" % "wiremock" % "2.2.2" % "test"
)

unmanagedResourceDirectories in Test <+= baseDirectory( _ / "features" )

packageName in Docker := "sdkman/vendor-proxy"

