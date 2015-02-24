package controllers

import play.api.Play._
import play.api.libs.ws.WS
import play.api.mvc.Controller
import security.AsVendor
import utils.{Environment, ResponseTransformation}

import scala.concurrent.ExecutionContext.Implicits.global

object Releases extends Controller with ResponseTransformation with Environment {

  def tokenHeader = "access_token" -> releaseAccessToken

  def release = AsVendor(parse.json) { request =>
    WS.url(releaseApiUrl).withHeaders(tokenHeader).post(request.body).map(transform)
  }
}
