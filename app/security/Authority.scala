package security

import play.api.mvc._
import repos.{ConsumerFields, ConsumerRepo}
import utils.TokenGenerator.sha256
import utils.{ErrorMarshalling, VendorProxyConfig}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object AsAdministrator extends ErrorMarshalling {

  val adminTokenHeaderNames = Seq("admin_token", "Admin-Token")

  def apply[T](parser: BodyParser[T], actionBuilder: ActionBuilder[Request, AnyContent])(
      f: Request[T] => Future[Result]
  )(implicit config: VendorProxyConfig): Action[T] =
    actionBuilder.async(parser)(secured(f))

  def secured[T](f: Request[T] => Future[Result])(implicit env: VendorProxyConfig): Request[T] => Future[Result] = {
    req: Request[T] =>
      adminTokenHeaderNames.flatMap(req.headers.get).headOption.fold(forbiddenF) {
        case s if s == env.secret => f(req)
        case _                    => forbiddenF
      }
  }
}

object AsConsumer extends ErrorMarshalling {

  val consumerKeyHeaderNames = Seq("consumer_key", "Consumer-Key")

  val consumerTokenHeaderNames = Seq("consumer_token", "Consumer-Token")

  def apply[T](parser: BodyParser[T], actionBuilder: ActionBuilder[Request, AnyContent])(
      f: (Request[T], String, Option[String]) => Future[Result]
  )(implicit cr: ConsumerRepo): Action[T] =
    actionBuilder.async(parser)(secured(f))

  def secured[T](
      fun: (Request[T], String, Option[String]) => Future[Result]
  )(implicit cr: ConsumerRepo): Request[T] => Future[Result] = { req: Request[T] =>
    consumerKeyHeaderNames.flatMap(req.headers.get).headOption.fold(forbiddenF) { key =>
      consumerTokenHeaderNames.flatMap(req.headers.get).headOption.fold(forbiddenF) { token =>
        cr.findConsumerFieldsByKeyAndToken(key, sha256(token)).flatMap { fields: Seq[ConsumerFields]  =>
          if (fields.nonEmpty) {
            val candidates = fields.map(_.candidate).mkString("|")
            val vendor = fields.flatMap(_.vendor).headOption
            fun(req, candidates, vendor)
          } else forbiddenF
        }
      }
    }
  }
}
