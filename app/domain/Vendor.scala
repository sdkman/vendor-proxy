package domain

import controllers.Vendors._
import play.api.libs.json.Json
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.core.commands.LastError
import utils.TokenGenerator.{generateConsumerKey, generateSHAToken}

import scala.concurrent.Future

case class Vendor(_id: String, name: String, token: String)

object Vendor {
  implicit val userFormat = Json.format[Vendor]

  def fromName(name: String): Vendor =
    Vendor(
      _id = generateConsumerKey(name),
      name = name,
      token = generateSHAToken(name))
}

trait VendorPersistence {

  import domain.Vendor.userFormat

  import scala.concurrent.ExecutionContext.Implicits.global

  val collName = "vendors"

  lazy val collection: JSONCollection = db.collection[JSONCollection](collName)

  def persist(v: Vendor): Future[LastError] = collection.insert(v)

  def succMsg(vendor: String) = s"Persisted $vendor"

  def errMsg(vendor: String) = s"Can not persist $vendor"

}

case class VendorPersistenceException(m: String) extends RuntimeException(m: String)