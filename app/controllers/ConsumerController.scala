package controllers

import com.google.inject.Inject
import domain.Consumers
import org.postgresql.util.PSQLException
import play.api.Logging
import play.api.libs.json.Json.toJson
import play.api.mvc._
import repos.ConsumerRepo
import security.AsAdministrator
import utils.TokenGenerator.{generateConsumerKey, sha256}
import utils.{ConsumerMarshalling, ErrorMarshalling, VendorProxyConfig}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ConsumerController @Inject()(val repo: ConsumerRepo, cc: ControllerComponents)(implicit val env: VendorProxyConfig)
  extends AbstractController(cc)
    with ConsumerMarshalling
    with ErrorMarshalling
    with Logging {

  def create = AsAdministrator(parse.json, controllerComponents) { req =>
    req.body.validate[CreateRequest].asOpt.fold(Future(BadRequest(badRequestMsg))) { consumerReq =>
      val consumer = Consumers.fromName(consumerReq.consumer)
      repo.persist(consumer.copy(token = sha256(consumer.token))).map { c =>
        logger.info(s"Successfully persisted Consumer: ${c.name} id: ${c.id}")
        Created(toJson(CreateResponse(consumer.id, consumer.token, consumer.name)))
      }.recover {
        case e: PSQLException =>
          val message = s"Could not persist Consumer: ${e.getServerErrorMessage}"
          logger.warn(message)
          Conflict(conflictMsg(message))
        case e: Throwable =>
          val message = s"Error on persisting Consumer: ${consumer.name} - err:${e.getMessage}"
          logger.error(message)
          InternalServerError(internalServerErrorMsg(e))
      }
    }
  }

  def revoke(name: String) = AsAdministrator(parse.default, controllerComponents) { req =>
    repo.deleteByName(name).map {
      case 1 =>
        Ok(toJson(DeleteResponse(generateConsumerKey(name), name, "consumer deleted")))
      case 0 =>
        NotFound
    }
  }
}
