package steps

import com.github.tomakehurst.wiremock.client.WireMock
import cucumber.api.scala.{EN, ScalaDsl}
import play.api.Logging
import play.test.Helpers.testServer
import support.{Db, World}
import support.World.{ServiceUpHost, ServiceUpPort}

import scala.collection.mutable

class Env extends ScalaDsl with EN with Logging {

  WireMock.configureFor(ServiceUpHost, ServiceUpPort)

  val app = testServer(9000)
  app.start()

  sys.addShutdownHook {
    logger.info("Shutting down test server...")
    app.stop()
  }

  Before { s =>
    Db.truncate()
    WireMock.reset()
    World.headers = mutable.Map[String, String](
      "Content-Type" -> "application/json",
      "Accept"       -> "application/json"
    )
  }
}
