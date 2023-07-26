package utils

import play.api.libs.json.Json

trait ConsumerMarshalling {

  case class CreateRequest(consumer: String, candidates: Seq[String], vendor: Option[String])

  case class CreateResponse(consumerKey: String, consumerToken: String, name: String)

  case class DeleteResponse(consumerKey: String, message: String)

  implicit val createRequestReads = Json.reads[CreateRequest]

  implicit val createResponseReads = Json.reads[CreateResponse]

  implicit val createResponseWrites = Json.writes[CreateResponse]

  implicit val deleteResponseReads = Json.reads[DeleteResponse]

  implicit val deleteResponseWrites = Json.writes[DeleteResponse]
}
