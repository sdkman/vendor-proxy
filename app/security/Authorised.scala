package security

import play.api.libs.json.JsValue
import play.api.libs.json.Json.toJson
import play.api.mvc.Results._
import play.api.mvc._
import utils.ErrorMarshalling

object Authorised extends ErrorMarshalling {

  def apply(parser: BodyParser[JsValue])(f: Request[JsValue] => Result) = Action(parser) { req =>
    req.headers.get("admin_token").fold(Forbidden(errorJson)) {
      case s if s == secret => f(req)
      case _ => Forbidden(errorJson)
    }
  }
  
  private val errorJson = toJson(ErrorMessage(401, "Not authorised to use this service."))

  private def secret = Option(System.getenv("ADMIN_TOKEN")).getOrElse("default_token")

}