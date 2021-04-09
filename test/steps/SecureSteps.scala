package steps

import cucumber.api.scala.{EN, ScalaDsl}
import org.scalatest.matchers.should.Matchers
import org.scalatest.concurrent.ScalaFutures
import support.{Db, World}
import utils.TokenGenerator

class SecureSteps extends ScalaDsl with EN with Matchers with ScalaFutures {

  Given("""^the Consumer owned by (.*) with Consumer Token (.*) for candidate (.*)$""") {
    (consumer: String, token: String, candidate: String) =>
      Db.saveConsumer(consumer, TokenGenerator.sha256(token), candidate.split(","))
  }

  Given("""^the Consumer owned by (.*) with Consumer Token (.*) for no candidates$""") {
    (consumer: String, token: String) =>
      Db.saveConsumer(consumer, TokenGenerator.sha256(token))
  }

  Given("""^the header (.*) (.*) is presented$""") { (key: String, value: String) =>
    World.headers.put(key, value)
  }
}
