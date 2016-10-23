package security

import play.api.libs.json.JsValue
import play.api.mvc._
import repos.ConsumerRepo
import utils.TokenGenerator.sha256
import utils.{VendorProxyConfig, ErrorMarshalling}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object AsAdministrator extends ErrorMarshalling {

  val adminTokenHeaderName = "admin_token"

  def apply(parser: BodyParser[JsValue])(f: Request[JsValue] => Future[Result])(implicit config: VendorProxyConfig) =
    Action.async(parser)(secured(f))

  def secured[T](f: Request[T] => Future[Result])(implicit env: VendorProxyConfig) = { (req: Request[T]) =>
    req.headers.get(adminTokenHeaderName).fold(forbiddenF) {
      case s if s == env.secret => f(req)
      case _ => forbiddenF
    }
  }
}

object AsConsumer extends ErrorMarshalling {

  val consumerKeyHeaderName = "consumer_key"

  val consumerTokenHeaderName = "consumer_token"

  def apply(parser: BodyParser[JsValue])(f: (Request[JsValue], String) => Future[Result])(implicit cr: ConsumerRepo) =
    Action.async(parser)(secured(f))

  def secured[T](fun: (Request[T], String) => Future[Result])(implicit cr: ConsumerRepo) = { (req: Request[T]) =>
    req.headers.get(consumerKeyHeaderName).fold(forbiddenF) { key =>
      req.headers.get(consumerTokenHeaderName).fold(forbiddenF) { token =>
        cr.findByKeyAndToken(key, sha256(token)).flatMap { (consumerNameO: Option[String]) =>
          consumerNameO.map(name => fun(req, name)).getOrElse(forbiddenF)
        }
      }
    }
  }
}

