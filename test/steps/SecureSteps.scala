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

  Given( """^a the Vendor "(.*?)" with Consumer Token "(.*?)"$"""){ (vendor: String, token: String) =>
    Mongo.saveVendor(vendorsColl, vendor, token)
  }

  Given("""^the Consumer Key "(.*?)" is presented$"""){ (key:String) =>
    World.consumerKey = key
  }

  Given("""^the Consumer Token "(.*?)" is presented$"""){ (token:String) =>
    World.consumerToken = token
  }
}
