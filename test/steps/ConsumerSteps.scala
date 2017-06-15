package steps

import com.github.tomakehurst.wiremock.client.WireMock
import cucumber.api.scala.{EN, ScalaDsl}
import org.scalatest.Matchers
import play.api.libs.json.Json
import support.World._
import support.{Db, Http, World}
import utils.{ConsumerMarshalling, ErrorMarshalling, TokenGenerator}

class ConsumerSteps extends ScalaDsl with EN with Matchers with ConsumerMarshalling with ErrorMarshalling {

  WireMock.configureFor(SERVICE_UP_HOST, SERVICE_UP_PORT)

  Before { s =>
    Db.cleanVendorsTable()
    WireMock.reset()
  }

  val ConsumerTokenPattern = """^[a-f0-9]{64}$""".r

  When("""^the Create Consumer endpoint (.*) is posted a request:$""") { (endpoint: String, json: String) =>
    val (rc, rb) = Http.postJson(endpoint, json.stripMargin)(World.headers.toMap)
    World.responseCode = rc
    World.responseBody = rb
  }

  Then("""^the returned status is (.*)$""") { (status: String) =>
    responseCode shouldBe statusCodes(status)
  }

  Then("""^the response contains a consumerKey of value (.*)$""") { (value: String) =>
    Json.parse(responseBody).validate[Response].asOpt match {
      case Some(actual) => actual.consumerKey shouldBe value
      case None => fail("No valid response found.")
    }
  }

  Then("""^the response contains a valid consumerToken$""") { () =>
    World.issuedToken = Json.parse(responseBody).as[Response].consumerToken
    World.issuedToken should fullyMatch regex ConsumerTokenPattern
  }

  Then("""the response contains a status of value (.*)""") { (status: Int) =>
    Json.parse(responseBody).validate[ErrorMessage].asOpt match {
      case Some(actual) => actual.status shouldBe status
      case None => fail("No valid status code found.")
    }
  }

  Then("""the response contains message (.*)""") { (message: String) =>
    Json.parse(responseBody).validate[ErrorMessage].asOpt match {
      case Some(actual) => actual.message should include(message)
      case None => fail("No valid message found.")
    }
  }

  Then("""the Consumer (.*) has been persisted""") { (consumer: String) =>
    withClue("The consumer was not persisted") {
      Db.vendorExists(consumer) shouldBe true
    }
  }

  Then("""the persisted Consumer (.*) has consumerKey (.*)""") { (consumer: String, consumerKey: String) =>
    Db.vendorKey(consumer) match {
      case Some(key) => key shouldBe consumerKey
      case None => fail("no consumer found")
    }
  }

  Then("""the persisted Consumer (.*) has a valid sha256 representation of the consumerToken""") { (consumer: String) =>
    Db.vendorToken(consumer) match {
      case Some(token) =>
        token should fullyMatch regex ConsumerTokenPattern
        withClue("Issued token was not equal to persisted token") {
          TokenGenerator.sha256(World.issuedToken) shouldBe token
        }
      case None =>
        fail("no consumer found")
    }
  }
}