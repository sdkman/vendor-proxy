package controllers

import play.api.Play._
import play.api.libs.ws.WS
import play.api.mvc.Controller
import security.AsVendor
import utils.{Environment, ResponseTransformation}

import scala.concurrent.ExecutionContext.Implicits.global

object Proxy extends Controller with ResponseTransformation with Environment {

  def post = AsVendor(parse.json) { request =>
    val service = request.path.substring(1)
    WS.url(apiUrl(service))
      .withHeaders(tokenHeader(service))
      .post(request.body)
      .map(transform)
  }
}
