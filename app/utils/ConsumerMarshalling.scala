package utils

import play.api.libs.json.Json

trait ConsumerMarshalling {
  case class Request(consumer: String)

  case class Response(consumerKey: String, consumerToken: String, name: String)

  implicit val requestReads = Json.reads[Request]

  implicit val responseReads = Json.reads[Response]

  implicit val responseWrites = Json.writes[Response]
}

