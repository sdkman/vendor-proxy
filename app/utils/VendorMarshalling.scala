package utils

import play.api.libs.json.Json

trait VendorMarshalling {
  case class Request(vendor: String)

  case class Response(consumerKey: String, consumerToken: String, name: String)

  implicit val reads = Json.reads[Request]

  implicit val writes = Json.writes[Response]
}

