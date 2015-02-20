package steps

import cucumber.api.scala.{EN, ScalaDsl}
import org.scalatest.ShouldMatchers
import org.scalatest.concurrent.ScalaFutures
import support.World._
import support.{Mongo, World}

class SecureSteps extends ScalaDsl with EN with ShouldMatchers with ScalaFutures {

  Before() { scenario =>
    vendorsColl = Mongo.createCollection(mongo, "vendors")
  }

  After() { scenario =>
    Mongo.dropCollection(vendorsColl)
  }

  Given( """^a the Vendor "(.*?)" with Access Token "(.*?)"$"""){ (vendor: String, token: String) =>
    Mongo.saveVendor(vendorsColl, vendor, token)
  }

  Given("""^the Access Token "(.*?)" is presented$"""){ (token:String) =>
    World.accessToken = token
  }
}
