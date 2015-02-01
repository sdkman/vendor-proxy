package controllers

import domain.VendorPersistence
import play.api.Play
import play.api.mvc._
import play.modules.reactivemongo.MongoController

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Vendors extends Controller with MongoController with VendorPersistence {

  def create(vendor: String) = Action.async { request =>
    Future {
      request.headers.get("access_token").fold(Forbidden(errmesg(vendor))) {
        case s if s == secret => Ok(succmesg(persist(vendor)))
        case _ => Forbidden(errmesg(vendor))
      }
    }
  }

  private def secret = Play.current.configuration.getString("access.token").getOrElse("invalid")
}
