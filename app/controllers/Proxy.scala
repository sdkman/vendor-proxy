package controllers

import play.api.Play._
import play.api.libs.ws.WS
import play.api.mvc.Controller
import security.AsVendor
import utils.{Environment, ResponseTransformation}

import scala.concurrent.ExecutionContext.Implicits.global

object Proxy extends Controller with ResponseTransformation with Environment {

  def execute(service: String) = AsVendor(parse.json) { request =>
    WS.url(apiUrl(service))
      .withHeaders(tokenHeader(service))
      .withMethod(request.method)
      .withBody(request.body)
      .execute()
      .map(transform)
  }
}
