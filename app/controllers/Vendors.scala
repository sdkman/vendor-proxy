package controllers

import domain.VendorPersistence
import play.api.libs.json.Json.toJson
import play.api.mvc._
import play.modules.reactivemongo.MongoController
import security.Authorised
import utils.{ErrorMarshalling, VendorMarshalling}

object Vendors extends Controller with MongoController with VendorPersistence with VendorMarshalling with ErrorMarshalling {

  def create = Authorised(parse.json) { req =>
    req.body.validate[Request].asOpt.fold(BadRequest(badRequestMsg)) { jsonReq =>
      val vendor = persist(jsonReq.vendor)
      Created(toJson(Response(vendor._id, vendor.token, vendor.name)))
    }
  }

}
