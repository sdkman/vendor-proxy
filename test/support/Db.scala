package support

import slick.driver.PostgresDriver.api._
import slick.lifted.TableQuery
import utils.TokenGenerator.generateConsumerKey

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

object Db {

  case class Vendor(id: String, name: String, token: String)

  class VendorsTable(tag: Tag) extends Table[Vendor](tag, "vendors") {
    def id = column[String]("id")

    def name = column[String]("name")

    def token = column[String]("token")

    def * = (id, name, token) <> (Vendor.tupled, Vendor.unapply)
  }

  def truncateVendorsTable() = exec(truncateVendorsTableAction)

  def vendorExists(vendor: String): Boolean = exec(vendorExistsAction(vendor)).isDefined

  def vendorKey(vendor: String): Option[String] = exec(vendorKeyAction(vendor))

  def vendorToken(vendor: String): Option[String] = exec(vendorTokenAction(vendor))

  def saveVendor(name: String, token: String) = exec(saveVendorAction(name, token))

  def exec[T](action: DBIO[T]): T = Await.result(database.run(action), 2 seconds)


  private lazy val VendorsTable = TableQuery[VendorsTable]

  private lazy val database = Database.forURL("jdbc:postgresql://postgres:5432/sdkman_vendor_proxy", "postgres", "")

  private val truncateVendorsTableAction = sqlu"TRUNCATE TABLE vendors"

  private def vendorExistsAction(name: String) = VendorsTable.filter(_.name === name).result.headOption

  private def vendorKeyAction(name: String) = VendorsTable.filter(_.name === name).map(_.id).result.headOption

  private def vendorTokenAction(name: String) = VendorsTable.filter(_.name === name).map(_.token).result.headOption

  private def saveVendorAction(name: String, token: String) =
    VendorsTable returning VendorsTable.map(_.id) into ((v, id) => v.copy(id = id)) +=
      Vendor(generateConsumerKey(name), name, token)
}