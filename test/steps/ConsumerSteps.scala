package steps

import cucumber.api.scala.{EN, ScalaDsl}
import org.scalatest.OptionValues
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.Json
import scalaj.http.{Http, HttpOptions}
import support.World._
import support.{Db, World}
import utils.{ConsumerMarshalling, ErrorMarshalling, TokenGenerator}

class ConsumerSteps
    extends ScalaDsl
    with EN
    with Matchers
    with OptionValues
    with ConsumerMarshalling
    with ErrorMarshalling {

  private val ConsumerTokenPattern = """^[a-f0-9]{64}$""".r

  And("""^the (.*) endpoint receives a PATCH request:$""") { (endpoint: String, json: String) =>
    val response = Http(AppHost + endpoint)
      .postData(json.stripMargin)
      .method("PATCH")
      .headers(World.headers.toMap)
      .option(HttpOptions.connTimeout(10000))
      .option(HttpOptions.readTimeout(10000))
      .asString
    World.responseCode = response.code
    World.responseBody = response.body
  }

  And("""^the (.*) endpoint receives a DELETE request""") { endpoint: String =>
    val response = Http(AppHost + endpoint)
      .method("DELETE")
      .headers(World.headers.toMap)
      .option(HttpOptions.connTimeout(10000))
      .option(HttpOptions.readTimeout(10000))
      .asString
    World.responseCode = response.code
    World.responseBody = response.body
  }

  And("""^the delete response contains a consumerKey of value (.*)$""") { key: String =>
    Json.parse(responseBody).validate[DeleteResponse].asOpt match {
      case Some(actual) => actual.consumerKey shouldBe key
      case None         => fail("no key found")
    }
  }

  And("""^the delete response contains an owner of value (.*)$""") { owner: String =>
    Json.parse(responseBody).validate[DeleteResponse].asOpt match {
      case Some(actual) => actual.owner shouldBe owner
      case None         => fail("owner not found")
    }
  }

  And("""^the delete response contains message (.*)$""") { message: String =>
    Json.parse(responseBody).validate[DeleteResponse].asOpt match {
      case Some(actual) => actual.message shouldBe message
      case None         => fail("no message found")
    }
  }

  And("""^the returned status is (.*)$""") { status: String =>
    println(responseBody)
    responseCode shouldBe statusCodes(status)
  }

  And("""^the create response contains a consumerKey of value (.*)$""") { value: String =>
    Json.parse(responseBody).validate[CreateResponse].asOpt match {
      case Some(actual) => actual.consumerKey shouldBe value
      case None         => fail("No key found")
    }
  }

  And("""^the create response contains a valid consumerToken$""") { () =>
    World.issuedToken = Json.parse(responseBody).as[CreateResponse].consumerToken
    World.issuedToken should fullyMatch regex ConsumerTokenPattern
  }

  And("""^the create response contains a reissued consumerToken$""") { () =>
    World.reissuedToken = Json.parse(responseBody).as[CreateResponse].consumerToken
    World.issuedToken should not be World.reissuedToken
    World.issuedToken = World.reissuedToken
  }

  And("""the response contains a status of value (.*)""") { status: Int =>
    Json.parse(responseBody).validate[ErrorMessage].asOpt match {
      case Some(actual) => actual.status shouldBe status
      case None         => fail("No status code found")
    }
  }

  And("""the response contains message (.*)""") { message: String =>
    Json.parse(responseBody).validate[ErrorMessage].asOpt match {
      case Some(actual) => actual.message should include(message)
      case None         => fail("No message found")
    }
  }

  And("""the Consumer (.*) has been persisted""") { consumer: String =>
    withClue("The consumer was not persisted") {
      Db.consumerExists(consumer) shouldBe true
    }
  }

  And("""the persisted Consumer (.*) has consumerKey (.*)""") { (consumer: String, consumerKey: String) =>
    Db.consumerKey(consumer) match {
      case Some(key) => key shouldBe consumerKey
      case None      => fail("no consumer found")
    }
  }

  And("""the persisted Consumer (.*) has a valid sha256 representation of the consumerToken""") { consumer: String =>
    Db.consumerToken(consumer) match {
      case Some(token) =>
        token should fullyMatch regex ConsumerTokenPattern
        withClue("Issued token was not equal to persisted token") {
          println(s"Issued token: ${World.issuedToken}")
          TokenGenerator.sha256(World.issuedToken) shouldBe token
        }
      case None =>
        fail("no consumer found")
    }
  }

  And("""the persisted Consumer (.*) has an associated candidate (.*)""") { (consumer: String, candidate: String) =>
    Db.consumerCandidates(consumer) should contain(candidate)
  }

  And("""the persisted Consumer (.*) has an associated vendor (.*)""") { (consumer: String, vendor: String) =>
    Db.consumerVendor(consumer).value shouldBe vendor
  }

  And("""the persisted Consumer (.*) does not have an associated candidate (.*)""") {
    (consumer: String, candidate: String) => Db.consumerCandidates(consumer) should not contain (candidate)
  }

  And("""an existing Consumer owned by (.*) for candidates (.*)""") { (owner: String, candidates: String) =>
    Db.saveConsumer(owner, TokenGenerator.generateConsumerKey(owner), candidates.split(","))
  }

  And("""no existing Consumer named (.*)""") { name: String =>
    //for information only
  }
}
