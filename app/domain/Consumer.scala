package domain

import controllers.Consumers._
import play.api.libs.json.Json
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.api.commands.WriteResult
import reactivemongo.bson.BSONDocument
import utils.Environment
import utils.TokenGenerator.{generateConsumerKey, generateSHAToken}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class Consumer(_id: String, name: String, token: String)
object Consumer {
  implicit val consumerWrites = Json.writes[Consumer]

  def fromName(name: String): Consumer =
    Consumer(_id = generateConsumerKey(name), name = name, token = generateSHAToken(name))
}

trait ConsumerPersistence {

  import play.modules.reactivemongo.json._

  lazy val collName = Environment.consumerCollection

  lazy val collection: JSONCollection = db.collection[JSONCollection](collName)

  def persist(c: Consumer): Future[WriteResult] = collection.insert(c)

  def findByKeyAndToken(key: String, token: String): Future[Option[String]] = {
    val query = BSONDocument("_id" -> key, "token" -> token)
    collection.find(query).one[BSONDocument].map(extractName)
  }

  private def extractName(bsonDocuments: Option[BSONDocument]): Option[String] =
    bsonDocuments.flatMap(_.getAs[String]("name"))

  def succMsg(consumer: String) = s"Persisted $consumer"

  def errMsg(consumer: String) = s"Can not persist $consumer"

}
