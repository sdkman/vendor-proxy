package security

import play.api.libs.json.JsValue
import play.api.mvc._
import repos.ConsumerRepo
import utils.TokenGenerator.sha256
import utils.{VendorProxyConfig, ErrorMarshalling}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object AsAdministrator extends ErrorMarshalling {

  val adminTokenHeaderNames = Seq("admin_token", "Admin-Token")

  def apply(parser: BodyParser[JsValue])(f: Request[JsValue] => Future[Result])(implicit config: VendorProxyConfig): Action[JsValue] =
    Action.async(parser)(secured(f))

  def secured[T](f: Request[T] => Future[Result])(implicit env: VendorProxyConfig): Request[T] => Future[Result] = { req: Request[T] =>
    adminTokenHeaderNames.flatMap(req.headers.get).headOption.fold(forbiddenF) {
      case s if s == env.secret => f(req)
      case _ => forbiddenF
    }
  }
}

object AsConsumer extends ErrorMarshalling {

  val consumerKeyHeaderNames = Seq("consumer_key", "Consumer-Key")

  val consumerTokenHeaderNames = Seq("consumer_token", "Consumer-Token")

  def apply(parser: BodyParser[JsValue])(f: (Request[JsValue], String) => Future[Result])(implicit cr: ConsumerRepo): Action[JsValue] =
    Action.async(parser)(secured(f))

  def secured[T](fun: (Request[T], String) => Future[Result])(implicit cr: ConsumerRepo): Request[T] => Future[Result] = { req: Request[T] =>
    consumerKeyHeaderNames.flatMap(req.headers.get).headOption.fold(forbiddenF) { key =>
      consumerTokenHeaderNames.flatMap(req.headers.get).headOption.fold(forbiddenF) { token =>
        cr.findByKeyAndToken(key, sha256(token)).flatMap { consumerNameO: Option[String] =>
          consumerNameO.map(name => fun(req, name)).getOrElse(forbiddenF)
        }
      }
    }
  }
}

