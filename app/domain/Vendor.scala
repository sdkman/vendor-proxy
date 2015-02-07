package domain

import controllers.Vendors._
import play.api.libs.json.Json
import play.modules.reactivemongo.json.collection.JSONCollection
import utils.TokenGenerator

case class Vendor(_id: String, name: String, token: String)

object Vendor {
  implicit val userFormat = Json.format[Vendor]
}

trait VendorPersistence {

  import domain.Vendor._
  import scala.concurrent.ExecutionContext.Implicits.global

  def collection: JSONCollection = db.collection[JSONCollection]("vendors")

  def persist(name: String): Vendor = {
    val oid = TokenGenerator.generateConsumerKey(name)
    val token = TokenGenerator.generateSHAToken(name)
    val vendor = Vendor(oid, name, token)
    collection.insert(vendor)
    vendor
  }

  def succMsg(vendor: String) = s"Persisted $vendor"

  def errMsg(vendor: String) = s"Can not persist $vendor"

}