package controllers

import com.google.inject.Inject
import domain.Consumers
import play.api.Logging
import play.api.libs.json.Json.toJson
import play.api.mvc._
import repos.ConsumerRepo
import security.AsAdministrator
import utils.TokenGenerator.{generateConsumerKey, sha256}
import utils.{ConsumerMarshalling, ErrorMarshalling, VendorProxyConfig}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ConsumerController @Inject() (val repo: ConsumerRepo, cc: ControllerComponents)(
    implicit val env: VendorProxyConfig
) extends AbstractController(cc)
    with ConsumerMarshalling
    with ErrorMarshalling
    with Logging {

  def create = AsAdministrator(parse.json, controllerComponents.actionBuilder) { req =>
    req.body.validate[CreateRequest].asOpt.fold(Future.successful(BadRequest(badRequestMsg))) { consumerReq =>
      val consumer = Consumers.fromOwner(consumerReq.consumer, consumerReq.candidates, consumerReq.vendor)
      repo
        .createOrUpdate(consumer.copy(token = sha256(consumer.token)))
        .map { id =>
          logger.info(
            s"Successfully persisted owner: ${consumer.owner} for candidates: ${consumer.candidates} with id: $id"
          )
          Created(toJson(CreateResponse(consumer.key, consumer.token, consumer.owner)))
        }
        .recover {
          case e: Throwable =>
            val message = s"Error on persisting Consumer: ${consumer.owner} - err:${e.getMessage}"
            logger.error(message)
            InternalServerError(internalServerErrorMsg(e))
        }
    }
  }

  def revoke(consumerKey: String) =
    AsAdministrator(parse.default, controllerComponents.actionBuilder) { _ =>
      repo.deleteByConsumerKey(consumerKey).map {
        case 1 =>
          Ok(toJson(DeleteResponse(consumerKey, "consumer deleted")))
        case 0 =>
          NotFound
      }
    }
}
