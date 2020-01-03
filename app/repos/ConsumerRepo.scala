package repos

import com.google.inject.Inject
import domain.Consumer
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._
import slick.lifted.TableQuery
import utils.VendorProxyConfig

import scala.concurrent.Future

class ConsumerRepo @Inject()(val dbConfigProvider: DatabaseConfigProvider, val config: VendorProxyConfig)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  lazy val ConsumersTable = TableQuery[ConsumersTable]

  def persist(c: Consumer): Future[Consumer] = db.run(ConsumersTable returning ConsumersTable.map(_.id) into ((c, id) => c.copy(id)) += c)

  def deleteByName(name: String): Future[Int] = db.run(ConsumersTable.filter(_.name === name).delete)

  private def findConsumer(key: String, token: String): DBIOAction[Option[String], NoStream, Effect.Read] =
    ConsumersTable
      .filter(_.id === key)
      .filter(_.token === token)
      .map(_.name)
      .result.headOption

  def findByKeyAndToken(key: String, token: String): Future[Option[String]] = db.run(findConsumer(key, token))

  class ConsumersTable(tag: Tag) extends Table[Consumer](tag, config.consumersTable) {
    def id = column[String]("id")

    def name = column[String]("name")

    def token = column[String]("token")

    def * = (id, name, token) <> (Consumer.tupled, Consumer.unapply)
  }
}