package steps

import com.github.tomakehurst.wiremock.client.WireMock
import cucumber.api.scala.{EN, ScalaDsl}
import support.{Db, World}
import support.World.{ServiceUpHost, ServiceUpPort}

import scala.collection.mutable

class Env extends ScalaDsl with EN {

  WireMock.configureFor(ServiceUpHost, ServiceUpPort)

  Before { s =>
    Db.truncate()
    WireMock.reset()
    World.headers = mutable.Map[String, String](
      "Content-Type" -> "application/json",
      "Accept" -> "application/json"
    )
  }
}
