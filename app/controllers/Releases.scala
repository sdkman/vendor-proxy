package controllers

import play.api.Play._
import play.api.libs.json.Json
import play.api.libs.json.Json.toJson
import play.api.libs.ws.WS
import play.api.mvc.Controller
import security.AsVendor
import utils.{Environment, ResponseTransformation}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class ReleaseRequest(candidate: String, version: String, url: String)

object ReleaseRequest {

  implicit val releaseRequestReads = Json.reads[ReleaseRequest]

  implicit val releaseRequestWrites = Json.writes[ReleaseRequest]

}

object Releases extends Controller with ResponseTransformation with Environment {

  def tokenHeader = "access_token" -> releaseAccessToken

  def create = AsVendor(parse.json) { request =>
    request.body.validate[ReleaseRequest].asOpt.fold(Future(badRequest)){ release =>
      WS.url(releaseApiUrl).withHeaders(tokenHeader).post(toJson(release)).map(transform)
    }
  }
}
