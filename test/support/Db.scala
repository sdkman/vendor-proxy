package support

import slick.jdbc.{GetResult, SetParameter}
import slick.jdbc.PostgresProfile.api._
import slick.lifted.TableQuery
import utils.TokenGenerator.generateConsumerKey

import java.sql.Types
import java.util.UUID
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.language.postfixOps

object Db extends DatabaseConnection {

  case class Vendor(id: String, name: String, token: String)

  def cleanVendorsTable() = exec(truncateVendorsTableAction)

  def vendorExists(vendor: String): Boolean = exec(vendorExistsAction(vendor)).isDefined

  def vendorKey(vendor: String): Option[String] = exec(vendorKeyAction(vendor))

  def vendorToken(vendor: String): Option[String] = exec(vendorTokenAction(vendor))

  def saveVendor(name: String, token: String) = exec(saveVendorAction(name, token))

  def exec[T](action: DBIO[T]): T = Await.result(database.run(action), 2 seconds)

  private implicit def helpersSlickGetResultUUID: GetResult[UUID] =
    GetResult(r => r.nextObject.asInstanceOf[UUID])

  private implicit def slickSetParameterUUID: SetParameter[UUID] =
    SetParameter { case (v, pp) => pp.setObject(v, Types.OTHER) }

  private val truncateVendorsTableAction = sqlu"TRUNCATE TABLE credentials CASCADE "

  private def vendorExistsAction(name: String) =
    sql"SELECT credential_id FROM candidates WHERE name = $name"
      .as[String]
      .headOption

  private def vendorKeyAction(name: String) =
    sql"SELECT key FROM credentials cred JOIN candidates can on cred.id = can.credential_id WHERE can.name = $name"
      .as[String]
      .headOption

  private def vendorTokenAction(name: String) =
    sql"SELECT token FROM credentials cred JOIN candidates can on cred.id = can.credential_id WHERE can.name = $name"
      .as[String]
      .headOption

  private def saveVendorAction(name: String, token: String) = {
    val credentialId = UUID.randomUUID()
    val key = generateConsumerKey(name)
    DBIO.seq(
      sqlu"INSERT INTO credentials(id, key, token, owner) VALUES ($credentialId, $key, $token, $name)",
      sqlu"INSERT INTO candidates(credential_id, name) VALUES ($credentialId, $name)"
    )
  }
}
