package utils

import play.api.libs.json.Json
import play.api.libs.json.Json._

trait ErrorMarshalling {
  case class ErrorMessage(statusCode: Int, message: String)

  implicit val errorMessageWrites = Json.writes[ErrorMessage]

  implicit val errorMessageReads = Json.reads[ErrorMessage]

  val forbiddenMsg = toJson(ErrorMessage(403, "Not authorised to use this service."))
  
  val badRequestMsg = toJson(ErrorMessage(400, "Malformed request body."))

  def conflictMsg(vendor: String) = toJson(ErrorMessage(409, s"Duplicate key for vendor: ${vendor}"))

  val internalServerErrorMsg = toJson(ErrorMessage(500, "Internal Server Error"))
}
