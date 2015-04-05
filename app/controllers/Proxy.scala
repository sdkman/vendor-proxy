package controllers

import play.api.Play._
import play.api.libs.ws.WS
import play.api.mvc.Controller
import security.AsConsumer
import utils.Environment._

import scala.concurrent.ExecutionContext.Implicits.global

object Proxy extends Controller {

  def execute(service: String) = AsConsumer(parse.json) { request =>
    WS.url(apiUrl(service))
      .withHeaders(tokenHeader(service))
      .withMethod(request.method)
      .withBody(request.body)
      .execute()
      .map(response => Status(response.status)(response.body))
  }
}
