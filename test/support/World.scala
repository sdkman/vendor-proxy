package support

import com.mongodb.casbah.MongoCollection

object World {

  val mongo = Mongo.primeDatabase("gvm")

  var coll: MongoCollection = _

  var adminToken: String = "invalid"

  var responseCode = 0

  var responseBody = "invalid"
}
