package support

import com.mongodb.casbah.MongoCollection

object World {

  Env.init()

  val statusCodes = Map(
    "CREATED" -> 201,
    "BAD_REQUEST" -> 400,
    "FORBIDDEN" -> 403,
    "CONFLICT" -> 409,
    "INTERNAL_SERVER_ERROR" -> 500,
    "BAD_GATEWAY" -> 502)

  val mongo = Mongo.primeDatabase("gvm")

  var vendorsColl: MongoCollection = _

  var adminToken: String = "invalid"

  var consumerKey: String = "invalid"

  var consumerToken: String = "invalid"

  var responseCode = 0

  var responseBody = "invalid"
}
