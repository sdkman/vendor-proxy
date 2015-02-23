package support

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration._
import cucumber.api.scala.ScalaDsl
import support.World._

object Env extends ScalaDsl {

  def init() = {}

  val SERVICE_UP_HOST = "localhost"
  val SERVICE_UP_PORT = 8080

  val wireMockServer = new WireMockServer(wireMockConfig().port(SERVICE_UP_PORT))
  wireMockServer.start()
  WireMock.configureFor(SERVICE_UP_HOST, SERVICE_UP_PORT)
}
