package steps

import com.github.tomakehurst.wiremock.client.WireMock._
import cucumber.api.scala.{EN, ScalaDsl}
import org.scalatest.matchers.should.Matchers
import play.api.libs.json.{Json, Reads}
import scalaj.http.{Http, HttpOptions}
import support.World
import support.World.AppHost

class ReleaseSteps extends ScalaDsl with EN with Matchers {

  When("""the remote release service is unavailable""") { () =>
    //nothing to do
  }

  When("""^posting JSON on the (.*) endpoint:$""") { (endpoint: String, payload: String) =>
    val response = Http(AppHost + endpoint)
      .postData(payload.stripMargin)
      .headers(World.headers.toMap)
      .option(HttpOptions.connTimeout(10000))
      .option(HttpOptions.readTimeout(10000))
      .asString
    World.responseCode = response.code
    World.responseBody = response.body
  }

  Then("""^the status received is (.*)$""") { status: String =>
    println(World.responseBody)
    World.responseCode shouldBe World.statusCodes(status)
  }

  sealed case class ApiResponse(status: Int, id: Option[String], message: String)

  implicit val responseReads: Reads[ApiResponse] = Json.reads[ApiResponse]

  Then("""^the response is:$""") { expectedJson: String =>
    val actual   = Json.parse(World.responseBody).as[ApiResponse]
    val expected = Json.parse(expectedJson.stripMargin).as[ApiResponse]
    actual shouldBe expected
  }

  And("""^the remote release service will return some (.*) response$""") { status: String =>
    stubFor(
      post(urlEqualTo("/versions"))
        .willReturn(
          aResponse()
            .withBody("""{"status": 201, "message": "blah blah"}""")
            .withStatus(World.statusCodes(status))
        )
    )
  }

  Given("""^the remote release service will return a (.*) response:$""") { (status: String, payload: String) =>
    stubFor(
      post(urlEqualTo("/versions"))
        .willReturn(
          aResponse()
            .withBody(payload.stripMargin)
            .withStatus(World.statusCodes(status))
        )
    )
  }

  Then("""^the remote release service expects a Candidates header (.*)""") { header: String =>
    verify(
      postRequestedFor(urlEqualTo("/versions"))
        .withHeader("Candidates", equalTo(header))
    )
  }

  Then("""^the remote release service expects a Vendor header (.*)""") { header: String =>
    verify(
      postRequestedFor(urlEqualTo("/versions"))
        .withHeader("Vendor", equalTo(header))
    )
  }

  Then("""^the remote release service expects an empty Candidates header""") {
    verify(
      postRequestedFor(urlEqualTo("/versions"))
        .withHeader("Candidates", equalTo(""))
    )
  }

  Then("""^the remote release service expects no Vendor header""") {
    verify(
      postRequestedFor(urlEqualTo("/versions"))
        .withoutHeader("Vendor")
    )
  }

  Then("""^the remote release service expects payload and appropriate headers:$""") { payload: String =>
    verify(
      postRequestedFor(urlEqualTo("/versions"))
        .withRequestBody(equalToJson(payload.stripMargin))
        .withHeader("Service-Token", equalTo("default_token"))
        .withHeader("Candidates", equalTo("groovy"))
    )
  }

  And("""^the remote release service expects NO posts$""") { () =>
    verify(0, postRequestedFor(urlEqualTo("/versions")))
  }

}
