package controllers

import play.api.Play._
import play.api.libs.json.Json
import play.api.libs.json.Json.toJson
import play.api.libs.ws.WS
import play.api.mvc.{Action, BodyParsers, Controller}
import security.SecuredEndpoint
import utils.ResponseTransformation

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class ReleaseRequest(candidate: String, version: String, url: String)

object ReleaseRequest {

  implicit val releaseRequestReads = Json.reads[ReleaseRequest]

  implicit val releaseRequestWrites = Json.writes[ReleaseRequest]

}

object Releases extends Controller with ResponseTransformation with SecuredEndpoint {

  def apiUrl = Option(System.getenv("RELEASE_API_URL")).getOrElse("http://localhost:8080/release")

  def create = Action.async(BodyParsers.parse.json) { request =>
    request.body.validate[ReleaseRequest].asOpt.fold {
      Future(BadRequest(customJson(400, "Malformed JSON payload")))
    } { release =>
      WS.url(apiUrl).withHeaders(accessToken).post(toJson(release)).map(transform)
    }
  }
}
