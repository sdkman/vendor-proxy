package steps

import cucumber.api.scala.{EN, ScalaDsl}
import org.scalatest.matchers.should.Matchers
import org.scalatest.concurrent.ScalaFutures
import support.{Db, World}
import utils.TokenGenerator

class SecureSteps extends ScalaDsl with EN with Matchers with ScalaFutures {

  Given( """^the Consumer (.*) with Consumer Token (.*)$""") { (consumer: String, token: String) =>
    Db.saveVendor(consumer, TokenGenerator.sha256(token))
  }

  Given("""^the header (.*) (.*) is presented$""") { (key: String, value: String) =>
    World.headers.put(key, value)
  }
}
