package controllers

import com.google.inject.Inject
import domain.{Consumer, Consumers}
import org.postgresql.util.PSQLException
import play.Logger
import play.api.libs.json.Json.toJson
import play.api.mvc._
import repos.ConsumerRepo
import security.AsAdministrator
import utils.TokenGenerator.sha256
import utils.{ConsumerMarshalling, ErrorMarshalling, VendorProxyConfig}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ConsumerController @Inject()(val cr: ConsumerRepo)(implicit val env: VendorProxyConfig)
  extends Controller
    with ConsumerMarshalling
    with ErrorMarshalling {

  def create = AsAdministrator(parse.json) { req =>
    req.body.validate[Request].asOpt.fold(Future(BadRequest(badRequestMsg))) { consumerReq =>
      val consumer = Consumers.fromName(consumerReq.consumer)
      cr.persist(consumer.copy(token = sha256(consumer.token))).map { c =>
          Logger.info(s"Successfully persisted Consumer: ${c.name} id: ${c.id}")
          Created(toJson(Response(c.id, c.token, c.name)))
      }.recover {
        case e: PSQLException =>
          val message = s"Could not persist Consumer: ${e.getServerErrorMessage}"
          Logger.warn(message)
          Conflict(conflictMsg(message))
        case e: Throwable =>
          val message = s"Error on persisting Consumer: ${consumer.name} - err:${e.getMessage}"
          Logger.error(message)
          InternalServerError(internalServerErrorMsg(e))
      }
    }
  }

}
