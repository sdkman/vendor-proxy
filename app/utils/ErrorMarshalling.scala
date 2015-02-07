package utils

import play.api.libs.json.Json

trait ErrorMarshalling {
  case class ErrorMessage(statusCode: Int, message: String)

  implicit val errorMessageWrites = Json.writes[ErrorMessage]

  implicit val errorMessageReads = Json.reads[ErrorMessage]
}
