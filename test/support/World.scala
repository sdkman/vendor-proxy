package support

import scala.collection.mutable

object World {

  val AppHost = "http://localhost:9000"

  val ServiceUpPort = 8080

  val statusCodes = Map(
    "OK"                    -> 200,
    "CREATED"               -> 201,
    "BAD_REQUEST"           -> 400,
    "FORBIDDEN"             -> 403,
    "NOT_FOUND"             -> 404,
    "CONFLICT"              -> 409,
    "INTERNAL_SERVER_ERROR" -> 500,
    "BAD_GATEWAY"           -> 502
  )

  var adminToken: String = "invalid"

  var issuedToken = "invalid"

  var reissuedToken = "invalid"

  var headers: mutable.Map[String, String] = _

  var responseCode = 0

  var responseBody = "invalid"
}
