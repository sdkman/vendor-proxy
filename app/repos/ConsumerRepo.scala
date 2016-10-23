package repos

import com.google.inject.Inject
import domain.Consumer
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.commands.WriteResult
import reactivemongo.bson.BSONDocument
import reactivemongo.play.json.collection.JSONCollection
import utils.VendorProxyConfig

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ConsumerRepo @Inject()(val reactiveMongoApi: ReactiveMongoApi, val config: VendorProxyConfig) {

  lazy val collName = config.consumerCollection

  lazy val collection = reactiveMongoApi.db.collection[JSONCollection](collName)

  import Consumer.consumerWrites
  def persist(c: Consumer): Future[WriteResult] = collection.insert(c)

  import play.modules.reactivemongo.json._
  def findByKeyAndToken(key: String, token: String): Future[Option[String]] = {
    val query = BSONDocument("_id" -> key, "token" -> token)
    collection.find(query).one[BSONDocument].map(extractName)
  }

  private def extractName(bsonDocuments: Option[BSONDocument]) = bsonDocuments.flatMap(_.getAs[String]("name"))

}
