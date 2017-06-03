package support

import com.github.tomakehurst.wiremock.client.WireMock
import cucumber.api.scala.ScalaDsl

object Env extends ScalaDsl {

  val SERVICE_UP_HOST = "localhost"
  val SERVICE_UP_PORT = 8080

  WireMock.configureFor(SERVICE_UP_HOST, SERVICE_UP_PORT)

  val statusCodes = Map(
    "CREATED" -> 201,
    "BAD_REQUEST" -> 400,
    "FORBIDDEN" -> 403,
    "NOT_FOUND" -> 404,
    "CONFLICT" -> 409,
    "INTERNAL_SERVER_ERROR" -> 500,
    "BAD_GATEWAY" -> 502)

  var adminToken: String = "invalid"

  var consumerKey: String = "invalid"

  var consumerToken: String = "invalid"

  var responseCode = 0

  var responseBody = "invalid"

  Before { s =>
    Db.truncateVendorsTable()
  }
}
