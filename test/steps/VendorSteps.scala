package steps

import cucumber.api.PendingException
import cucumber.api.scala.{EN, ScalaDsl}
import org.scalatest.ShouldMatchers
import play.api.libs.json.Json
import support.World.{responseBody, responseCode, _}
import support.{World, Http}

case class VendorPayload(consumerKey: String, consumerToken: String)
object VendorPayload {
  implicit val payloadReads = Json.format[VendorPayload]
}

class VendorSteps extends ScalaDsl with EN with ShouldMatchers {

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

  Then("""^the status is "(.*?)"$"""){ (status: String) =>
    responseCode shouldBe statusCodes(status)
  }
  
  Then("""^the payload contains "(.*?)" of value "(.*?)"$"""){ (key: String, value: String) =>
    val actual = Json.parse(responseBody).as[VendorPayload]
    actual.consumerKey shouldBe key
  }

  Then("""^the payload contains a valid "(.*?)"$"""){ (arg0:String) =>
    val actual = Json.parse(responseBody).as[VendorPayload]
    actual.consumerToken.length shouldBe 64
  }

}