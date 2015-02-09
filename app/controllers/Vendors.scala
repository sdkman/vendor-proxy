package controllers

import domain.{VendorPersistence, VendorPersistenceException}
import play.api.libs.json.Json.toJson
import play.api.mvc._
import play.modules.reactivemongo.MongoController
import security.Authorised
import utils.{ErrorMarshalling, VendorMarshalling}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Vendors extends Controller with MongoController with VendorPersistence with VendorMarshalling with ErrorMarshalling {

  def create = Authorised(parse.json) { req =>
    req.body.validate[Request].asOpt.fold(Future(BadRequest(badRequestMsg))) { vendorReq =>
      for {
        vendorO <- persist(vendorReq.vendor)
        vendor = vendorO.getOrElse(throw VendorPersistenceException("persistence issue"))
      } yield Created(toJson(Response(vendor._id, vendor.token, vendor.name)))
    }
  }

}
