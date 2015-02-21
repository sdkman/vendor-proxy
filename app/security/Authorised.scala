package security

import play.api.libs.json.JsValue
import play.api.mvc.Results._
import play.api.mvc._
import utils.ErrorMarshalling

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Authorised extends ErrorMarshalling {

  def apply(parser: BodyParser[JsValue])(f: Request[JsValue] => Future[Result]) = Action.async(parser)(secured(f))
  
  private def secured[T](f: Request[T] => Future[Result]) = { req: Request[T] =>
    req.headers.get("admin_token").fold(forbiddenF) {
      case s if s == secret => f(req)
      case _ => forbiddenF
    }
  }

  private val forbiddenF = Future(Forbidden(forbiddenMsg))
  
  private def secret = Option(System.getenv("ADMIN_TOKEN")).getOrElse("default_token")

}