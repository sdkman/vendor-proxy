package security

import play.api.libs.json.JsValue
import play.api.mvc._
import play.api.mvc.Results._

object Authorised {

  def apply(parser: BodyParser[JsValue])(f: Request[JsValue] => Result) = Action(parser) { request =>
    request.headers.get("admin_token").fold(Forbidden(message)) {
      case s if s == secret => f(request)
      case _ => Forbidden(message)
    }
  }
  
  private val message = "Not authorised to use this service."

  private def secret = Option(System.getenv("ADMIN_TOKEN")).getOrElse("default_token")

}