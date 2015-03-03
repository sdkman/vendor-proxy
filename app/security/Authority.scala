package security

import domain.ConsumerPersistence
import play.api.libs.json.JsValue
import play.api.mvc.Results._
import play.api.mvc._
import utils.{Environment, ErrorMarshalling}
import utils.TokenGenerator.sha256

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait Authority extends ErrorMarshalling {

  def apply(parser: BodyParser[JsValue])(f: Request[JsValue] => Future[Result]) = Action.async(parser)(secured(f))

  def secured[T](f: Request[T] => Future[Result]): (Request[T]) => Future[Result]

  val forbiddenF = Future(Forbidden(forbiddenMsg))

}

object AsAdministrator extends Authority with Environment {

  val adminTokenHeaderName = "admin_token"

  override def secured[T](f: Request[T] => Future[Result]) = { req: Request[T] =>
    req.headers.get(adminTokenHeaderName).fold(forbiddenF) {
      case s if s == secret => f(req)
      case _ => forbiddenF
    }
  }
}

object AsConsumer extends Authority with ConsumerPersistence {

  val consumerKeyHeaderName = "consumer_key"

  val consumerTokenHeaderName = "consumer_token"

  override def secured[T](fun: (Request[T]) => Future[Result]) = { req: Request[T] =>
    req.headers.get(consumerKeyHeaderName).fold(forbiddenF) { key =>
      req.headers.get(consumerTokenHeaderName).fold(forbiddenF) { token =>
        findByKeyAndToken(key, sha256(token)).flatMap { consumers =>
          if(consumers.nonEmpty) fun(req) else forbiddenF
        }
      }
    }
  }
}

