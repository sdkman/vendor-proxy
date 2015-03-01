package domain

import controllers.Consumers._
import play.api.libs.json.Json
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.bson.BSONDocument
import reactivemongo.core.commands.LastError
import utils.TokenGenerator.{generateConsumerKey, generateSHAToken}

import scala.concurrent.Future

case class Consumer(_id: String, name: String, token: String)

object Consumer {
  implicit val consumerWrites = Json.writes[Consumer]

  def fromName(name: String): Consumer =
    Consumer(_id = generateConsumerKey(name), name = name, token = generateSHAToken(name))
}

trait ConsumerPersistence {

  import domain.Consumer.consumerWrites
  import play.modules.reactivemongo.json.BSONFormats.BSONDocumentFormat
  import scala.concurrent.ExecutionContext.Implicits.global

  val collName = "consumers"

  lazy val collection: JSONCollection = db.collection[JSONCollection](collName)

  def persist(v: Consumer): Future[LastError] = collection.insert(v)

  def findByKeyAndToken(key: String, token: String): Future[List[BSONDocument]] = {
    val query = BSONDocument("_id" -> key, "token" -> token)
    collection.find(query).cursor[BSONDocument].collect[List]()
  }

  def succMsg(consumer: String) = s"Persisted $consumer"

  def errMsg(consumer: String) = s"Can not persist $consumer"

}

case class ConsumerPersistenceException(m: String) extends RuntimeException(m: String)
