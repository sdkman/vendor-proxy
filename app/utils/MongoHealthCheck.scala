package utils

import com.google.inject.{Inject, Singleton}
import play.api.Logger
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.bson.BSONDocument
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class MongoHealthCheck @Inject()(val reactiveMongoApi: ReactiveMongoApi) {

  import play.modules.reactivemongo.json._

  lazy val appColl = Environment.applicationCollection

  lazy val collection = reactiveMongoApi.db.collection[JSONCollection](appColl)

  def probeDatabase(): Future[_] = {
    Logger.info("Performing db initialisation...")
    collection.find(BSONDocument("alive" -> "OK")).one[BSONDocument]
  }

  probeDatabase()
}
