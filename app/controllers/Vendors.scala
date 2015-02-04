package controllers

import domain.VendorPersistence
import play.api.libs.json.Json
import play.api.mvc._
import play.modules.reactivemongo.MongoController

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Vendors extends Controller with MongoController with VendorPersistence {

  case class VendorRequest(vendor: String)
  object VendorRequest {
    implicit val reads = Json.format[VendorRequest]
  }

  def create = Action.async { request =>
    Future {
      val vendor = "groovy"
      request.headers.get("admin_token").fold(Forbidden(errmesg(vendor))) {
        case s if s == secret => Created(succmesg(persist(vendor)))
        case _ => Forbidden(errmesg(vendor))
      }
    }
  }

  private def secret = Option(System.getenv("ADMIN_TOKEN")).getOrElse("invalid")
}
