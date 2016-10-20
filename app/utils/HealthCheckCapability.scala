package utils

import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.bson.BSONDocument
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait HealthCheckCapability {
  import play.modules.reactivemongo.json._

  val reactiveMongoApi: ReactiveMongoApi

  lazy val appColl = Environment.applicationCollection

  lazy val collection = reactiveMongoApi.db.collection[JSONCollection](appColl)

  def probeDatabase(): Future[_] = collection.find(BSONDocument("alive" -> "OK")).one[BSONDocument]

}
