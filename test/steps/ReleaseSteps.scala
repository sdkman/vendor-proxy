package steps

import com.github.tomakehurst.wiremock.client.WireMock
import cucumber.api.PendingException
import cucumber.api.scala.{EN, ScalaDsl}
import org.scalatest.ShouldMatchers
import play.api.libs.json.Json
import support.{Env, Http}
import support.World._

class ReleaseSteps extends ScalaDsl with EN with ShouldMatchers {

  Before() { s =>
    Env.init()
  }

  And("""a Release microservice to proxy""") { () =>
    throw new PendingException("pow")
  }

  When("""^posting JSON on the "(.*?)" endpoint:$"""){ (endpoint: String, payload: String) =>
    val secureHeader = "access_token" -> accessToken
    val (rc, rb) = Http.postJson(endpoint, payload.stripMargin, secureHeader)
    responseCode = rc
    responseBody = rb
  }

  Then("""^the status received is "(.*?)"$"""){ (status: String) =>
    responseCode shouldBe statusCodes(status)
  }

  sealed case class ApiResponse(status: Int, id: Option[String], message: String)

  implicit val responseReads = Json.reads[ApiResponse]

  Then("""^the response is:$"""){ (expectedJson: String) =>
    val actual = Json.parse(responseBody).as[ApiResponse]
    val expected = Json.parse(expectedJson.stripMargin).as[ApiResponse]
    actual shouldBe expected
  }

  Given("""^the remote release service returns a "(.*)" response:$"""){ (status: String, payload: String) =>
    import WireMock._
    stubFor(post(urlEqualTo("/release"))
      .willReturn(aResponse()
          .withBody(payload.stripMargin)
          .withStatus(statusCodes(status))
      )
    )
  }

}
