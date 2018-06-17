package steps

import com.github.tomakehurst.wiremock.client.WireMock
import cucumber.api.scala.{EN, ScalaDsl}
import support.Db
import support.World.{SERVICE_UP_HOST, SERVICE_UP_PORT}

class Env extends ScalaDsl with EN {

  WireMock.configureFor(SERVICE_UP_HOST, SERVICE_UP_PORT)

  Before { s =>
    Db.cleanVendorsTable()
    WireMock.reset()
  }
}
