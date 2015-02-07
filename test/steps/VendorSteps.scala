package steps

import cucumber.api.scala.{EN, ScalaDsl}
import org.scalatest.ShouldMatchers
import play.api.libs.json.Json
import support.World.{responseBody, responseCode, _}
import support.{Http, World}
import utils.VendorMarshalling

class VendorSteps extends ScalaDsl with EN with ShouldMatchers with VendorMarshalling {

  val statusCodes = Map("CREATED" -> 201)

  Given("""^the valid Admin Token "(.*?)" is presented$"""){ (token: String) =>
    World.adminToken = token
  }

  Given("""^an Environment Variable "(.*)"$"""){ (envVar: String) =>
    System.getenv("ADMIN_TOKEN") shouldBe envVar
  }

  When("""^the Create Vendor endpoint "(.*)" is posted a request:$"""){ (endpoint: String, json: String) =>
    val (rc, rb) = Http.postJson(endpoint, json.stripMargin, adminToken)
    World.responseCode = rc
    World.responseBody = rb
  }

  Then("""^the returned status is "(.*?)"$"""){ (status: String) =>
    responseCode shouldBe statusCodes(status)
  }

  implicit val responseRead = Json.reads[Response]

  Then("""^the payload contains a consumerKey of value "(.*?)"$"""){ (value: String) =>
    Json.parse(responseBody).validate[Response].asOpt match {
      case Some(actual) => actual.consumerKey shouldBe value
      case None => fail("No valid response found.")
    }
  }

  Then("""^the payload contains a valid consumerToken$"""){ () =>
    val actual = Json.parse(responseBody).as[Response]
    actual.consumerToken.length shouldBe 64
  }

}