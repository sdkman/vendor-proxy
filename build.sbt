import com.typesafe.config._

enablePlugins(JavaServerAppPackaging)

enablePlugins(DockerPlugin)

val conf = ConfigFactory.parseFile(new File("conf/application.conf")).resolve()

version := conf.getString("application.version")

name := """vendor-proxy"""

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.8"

routesGenerator := InjectedRoutesGenerator

val reactiveMongoVer = "0.11.14"
libraryDependencies ++= Seq(
  ws,
  "org.reactivemongo" %% "play2-reactivemongo" % s"$reactiveMongoVer-play24",
  "org.scalatest" %% "scalatest" % "3.0.0" % "test",
  "info.cukes" %% "cucumber-scala" % "1.2.5" % "test",
  "info.cukes" % "cucumber-junit" % "1.2.5" % "test",
  "org.scalaj" %% "scalaj-http" % "0.3.16" % "test",
  "org.mongodb" %% "casbah" % "3.1.1" % "test",
  "com.github.tomakehurst" % "wiremock" % "2.2.2"
)

unmanagedResourceDirectories in Test <+= baseDirectory( _ / "features" )

packageName in Docker := "sdkman/vendor-proxy"

