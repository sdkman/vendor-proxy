package domain

import play.api.libs.json.Json

case class ReleaseRequest(candidate: String, version: String, url: String)

object ReleaseRequest {

  implicit val releaseRequestReads = Json.reads[ReleaseRequest]

  implicit val releaseRequestWrites = Json.writes[ReleaseRequest]

}

case class ApiResponse(status: Int, id: Option[String], message: String)
