package controllers

import com.google.inject.Inject
import domain.Consumer
import play.Logger
import play.api.libs.json.Json.toJson
import play.api.mvc._
import reactivemongo.core.commands.LastError
import repos.ConsumerRepo
import security.AsAdministrator
import utils.TokenGenerator.sha256
import utils.{ConsumerMarshalling, ErrorMarshalling, VendorProxyConfig}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

class Consumers @Inject() (val cr: ConsumerRepo)(implicit val env: VendorProxyConfig)
  extends Controller
    with ConsumerMarshalling
    with ErrorMarshalling {

  def create = AsAdministrator(parse.json) { req =>
    req.body.validate[Request].asOpt.fold(Future(BadRequest(badRequestMsg))) { consumerReq =>
      val consumer = Consumer.fromName(consumerReq.consumer)
      cr.persist(consumer.copy(token = sha256(consumer.token))).map {
        case le if le.ok =>
          Logger.info(s"Successfully persisted Consumer: ${consumer.name}")
          Created(toJson(Response(consumer._id, consumer.token, consumer.name)))
      }.recover {
        case LastError(_, _, Some(11000), _, _, _, _) =>
          Logger.error(s"Conflict on persisting Consumer: ${consumer.name}")
          Conflict(conflictMsg(consumer.name))
        case LastError(ok, err, code, errMsg, doc, n, updatedExisting) =>
          val message = s"Error on persisting Consumer: ${consumer.name} - code:${code.getOrElse("no code")} err:${err.getOrElse("no message")}"
          Logger.error(message)
          InternalServerError(message)
      }
    }
  }

}
