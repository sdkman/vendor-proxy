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
    consumersColl = Mongo.createCollection(mongo, "consumers")
  }

  After() { scenario =>
    Mongo.dropCollection(consumersColl)
    WireMock.reset()
  }

  Given( """^a the Consumer "(.*?)" with Consumer Token "(.*?)"$"""){ (consumer: String, token: String) =>
    Mongo.saveConsumer(consumersColl, consumer, TokenGenerator.sha256(token))
  }

  Given("""^the Consumer Key "(.*?)" is presented$"""){ (key:String) =>
    World.consumerKey = key
  }

  Given("""^the Consumer Token "(.*?)" is presented$"""){ (token:String) =>
    World.consumerToken = token
  }
}
