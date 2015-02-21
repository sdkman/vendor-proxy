package utils

import controllers.Releases._
import play.api.libs.json.Json
import play.api.libs.json.Json._
import play.api.libs.ws.WSResponse
import play.api.mvc.Result

trait ResponseTransformation {

  def transform(response: WSResponse): Result = {
    response.status match {
      case 200 => Ok(json(response))
      case 201 => Created(json(response))
      case 400 => BadRequest(json(response))
      case 403 => Forbidden(json(response))
      case 409 => Conflict(json(response))
      case 500 => InternalServerError(customJson(500, "Internal Server Error"))
      case _ => NotImplemented(customJson(501, "NotImplemented"))
    }
  }

  case class ApiResponse(status: Int, id: Option[String], message: String)

  implicit val writes = Json.writes[ApiResponse]

  def json(response: WSResponse) = toJson(
    ApiResponse(
      status = response.status,
      id = (response.json \ "id").asOpt[String],
      message = (response.json \ "message").as[String]))

  def customJson(status: Int, message: String) = toJson(
    ApiResponse(
      status = status,
      id = None,
      message = message))

}
