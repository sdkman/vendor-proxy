package steps

import com.github.tomakehurst.wiremock.client.WireMock
import cucumber.api.scala.{EN, ScalaDsl}
import org.scalatest.ShouldMatchers
import org.scalatest.concurrent.ScalaFutures
import support.World._
import support.{Mongo, World}
import utils.TokenGenerator

class SecureSteps extends ScalaDsl with EN with ShouldMatchers with ScalaFutures {

  Before() { scenario =>
    vendorsColl = Mongo.createCollection(mongo, "vendors")
  }

  After() { scenario =>
    Mongo.dropCollection(vendorsColl)
    WireMock.reset()
  }

  Given( """^a the Vendor "(.*?)" with Consumer Token "(.*?)"$"""){ (vendor: String, token: String) =>
    Mongo.saveVendor(vendorsColl, vendor, TokenGenerator.sha256(token))
  }

  Given("""^the Consumer Key "(.*?)" is presented$"""){ (key:String) =>
    World.consumerKey = key
  }

  Given("""^the Consumer Token "(.*?)" is presented$"""){ (token:String) =>
    World.consumerToken = token
  }
}
