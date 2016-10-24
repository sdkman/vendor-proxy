package utils

import play.api.libs.json.Json
import play.api.libs.json.Json._
import play.api.mvc.Results._
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait ErrorMarshalling {
  case class ErrorMessage(status: Int, message: String)

  implicit val errorMessageWrites = Json.writes[ErrorMessage]

  implicit val errorMessageReads = Json.reads[ErrorMessage]

  val forbiddenMsg = toJson(ErrorMessage(403, "Not authorised to use this service."))

  val forbiddenF = Future(Forbidden(forbiddenMsg))

  val badRequestMsg = toJson(ErrorMessage(400, "Malformed request body."))

  def conflictMsg(consumer: String) = toJson(ErrorMessage(409, s"Duplicate key for consumer: $consumer"))

  def clientError(statusCode: Int, message: String) = toJson(ErrorMessage(statusCode, message))

  def internalServerErrorMsg(e: Throwable) = toJson(ErrorMessage(500, e.toString))

  def serviceUnavailableMsg(e: Throwable) = toJson(ErrorMessage(503, e.toString))
}
