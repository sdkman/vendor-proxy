package steps

import cucumber.api.scala.{EN, ScalaDsl}
import org.scalatest.Matchers
import org.scalatest.concurrent.ScalaFutures
import support.{Db, Env}
import utils.TokenGenerator

class SecureSteps extends ScalaDsl with EN with Matchers with ScalaFutures {

  Given( """^a the Consumer "(.*?)" with Consumer Token "(.*?)"$""") { (consumer: String, token: String) =>
    Db.saveVendor(consumer, TokenGenerator.sha256(token))
  }

  Given("""^the Consumer Key "(.*?)" is presented$""") { (key: String) =>
    Env.consumerKey = key
  }

  Given("""^the Consumer Token "(.*?)" is presented$""") { (token: String) =>
    Env.consumerToken = token
  }
}
