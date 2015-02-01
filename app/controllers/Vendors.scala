package controllers

import domain.Vendor
import play.api.Play
import play.api.mvc._
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection
import utils.TokenGenerator

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Vendors extends Controller with MongoController {

  import domain.Vendor._

  def create(vendor: String) = Action.async { request =>
    Future {
      request.headers.get("access_token").fold(Forbidden(errmesg(vendor))) {
        case s if s == secret => Ok(succmesg(persist(vendor)))
        case _ => Forbidden(errmesg(vendor))
      }
    }
  }

  private def collection: JSONCollection = db.collection[JSONCollection]("vendors")

  private def persist(vendor: String) = {
    collection.insert(Vendor(vendor, TokenGenerator.generateSHAToken(vendor)))
    vendor
  }

  private def succmesg(vendor: String) = s"Persisted $vendor"

  private def errmesg(vendor: String) = s"Can not persist $vendor"

  private def secret = Play.current.configuration.getString("access.token").getOrElse("invalid")
}
