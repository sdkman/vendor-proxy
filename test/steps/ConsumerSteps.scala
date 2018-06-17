package steps

import cucumber.api.scala.{EN, ScalaDsl}
import org.scalatest.Matchers
import play.api.libs.json.Json
import scalaj.http.{Http, HttpOptions}
import support.World._
import support.{Db, World}
import utils.{ConsumerMarshalling, ErrorMarshalling, TokenGenerator}

class ConsumerSteps extends ScalaDsl with EN with Matchers with ConsumerMarshalling with ErrorMarshalling {

  val ConsumerTokenPattern = """^[a-f0-9]{64}$""".r

  When("""^the (.*) endpoint receives a POST request:$""") { (endpoint: String, json: String) =>
    val response = Http(appHost + endpoint)
      .postData(json.stripMargin)
      .headers(World.headers.toMap)
      .option(HttpOptions.connTimeout(10000))
      .option(HttpOptions.readTimeout(10000))
      .asString
    World.responseCode = response.code
    World.responseBody = response.body
  }

  When("""^the (.*) endpoint receives a DELETE request""") { endpoint: String =>
    val response = Http(appHost + endpoint).method("DELETE")
      .headers(World.headers.toMap)
      .option(HttpOptions.connTimeout(10000))
      .option(HttpOptions.readTimeout(10000))
      .asString
    World.responseCode = response.code
    World.responseBody = response.body
  }

  Then("""^the delete response contains a consumerKey of value (.*)$""") { key: String =>

  }

  Then("""^the delete response contains a name of value (.*)$""") { name: String =>

  }

  Then("""^the delete response contains message (.*)$""") { message: String =>

  }

  Then("""^the returned status is (.*)$""") { status: String =>
    responseCode shouldBe statusCodes(status)
  }

  Then("""^the create response contains a consumerKey of value (.*)$""") { value: String =>
    Json.parse(responseBody).validate[CreateResponse].asOpt match {
      case Some(actual) => actual.consumerKey shouldBe value
      case None => fail("No valid response found.")
    }
  }

  Then("""^the create response contains a valid consumerToken$""") { () =>
    World.issuedToken = Json.parse(responseBody).as[CreateResponse].consumerToken
    World.issuedToken should fullyMatch regex ConsumerTokenPattern
  }

  Then("""the response contains a status of value (.*)""") { status: Int =>
    Json.parse(responseBody).validate[ErrorMessage].asOpt match {
      case Some(actual) => actual.status shouldBe status
      case None => fail("No valid status code found.")
    }
  }

  Then("""the response contains message (.*)""") { message: String =>
    Json.parse(responseBody).validate[ErrorMessage].asOpt match {
      case Some(actual) => actual.message should include(message)
      case None => fail("No valid message found.")
    }
  }

  Then("""the Consumer (.*) has been persisted""") { consumer: String =>
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

  Then("""the persisted Consumer (.*) has a valid sha256 representation of the consumerToken""") { consumer: String =>
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