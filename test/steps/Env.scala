package steps

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.options
import cucumber.api.scala.{EN, ScalaDsl}
import play.api.Logging
import play.test.Helpers.testServer
import support.World.ServiceUpPort
import support.{Db, World}

import scala.collection.mutable

class Env extends ScalaDsl with EN with Logging {

  val wireMockServer = new WireMockServer(options().port(ServiceUpPort))
  wireMockServer.start()

  val app = testServer(9000)
  app.start()

  sys.addShutdownHook {
    logger.info("Shutting down test server...")
    app.stop()
    wireMockServer.stop()
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
