import com.typesafe.config._

enablePlugins(JavaServerAppPackaging)

enablePlugins(DockerPlugin)

val conf = ConfigFactory.parseFile(new File("conf/application.conf")).resolve()

version := conf.getString("application.version")

name := """security-proxy"""

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"

libraryDependencies ++= Seq(
  "org.reactivemongo" %% "play2-reactivemongo" % "0.10.5.0.akka23",
  ws,
  "org.scalatest" %% "scalatest" % "2.2.1" % "test",
  "info.cukes" %% "cucumber-scala" % "1.1.8" % "test",
  "info.cukes" % "cucumber-junit" % "1.1.8" % "test",
  "org.scalaj" %% "scalaj-http" % "0.3.16" % "test",
  "org.mongodb" %% "casbah" % "2.7.3" % "test",
  "com.github.tomakehurst" % "wiremock" % "1.53"
)

unmanagedResourceDirectories in Test <+= baseDirectory( _ / "features" )

packageName in Docker := "sdkman/secure-proxy"

