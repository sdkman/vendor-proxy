package security

import domain.ConsumerPersistence
import play.api.libs.json.JsValue
import play.api.mvc._
import reactivemongo.bson.BSONDocument
import utils.TokenGenerator.sha256
import utils.{Environment, ErrorMarshalling}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object AsAdministrator extends ErrorMarshalling {

  val adminTokenHeaderName = "admin_token"

  def apply(parser: BodyParser[JsValue])(f: Request[JsValue] => Future[Result]) = Action.async(parser)(secured(f))

  def secured[T](f: Request[T] => Future[Result]) = { (req: Request[T]) =>
    req.headers.get(adminTokenHeaderName).fold(forbiddenF) {
      case s if s == Environment.secret => f(req)
      case _ => forbiddenF
    }
  }
}

object AsConsumer extends ErrorMarshalling with ConsumerPersistence {

  val consumerKeyHeaderName = "consumer_key"

  val consumerTokenHeaderName = "consumer_token"

  def apply(parser: BodyParser[JsValue])(f: (Request[JsValue], String) => Future[Result]) = Action.async(parser)(secured(f))

  def secured[T](fun: (Request[T], String) => Future[Result]) = { (req: Request[T]) =>
    req.headers.get(consumerKeyHeaderName).fold(forbiddenF) { key =>
      req.headers.get(consumerTokenHeaderName).fold(forbiddenF) { token =>
        findByKeyAndToken(key, sha256(token)).flatMap { (consumers: List[BSONDocument]) =>
          if(consumers.nonEmpty) {
            val consumerName = consumers.head.getAs[String]("name").getOrElse("not_found")
            fun(req, consumerName)
          } else forbiddenF
        }
      }
    }
  }
}

