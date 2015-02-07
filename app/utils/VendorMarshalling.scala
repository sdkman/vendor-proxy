package utils

import play.api.libs.json.Json

trait VendorMarshalling {
  case class Request(vendor: String)

  case class Response(consumerKey: String, consumerToken: String, name: String)

  implicit val requestReads = Json.reads[Request]

  implicit val responseReads = Json.reads[Response]

  implicit val responseWrites = Json.writes[Response]
}

