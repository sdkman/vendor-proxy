package repos

import com.google.inject.Inject
import play.api.Logger
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.collections.bson.BSONCollection
import utils.VendorProxyConfig

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class HealthCheckRepo @Inject()(val reactiveMongoApi: ReactiveMongoApi, val config: VendorProxyConfig) {

  lazy val appColl = config.applicationCollection

  lazy val collection = reactiveMongoApi.db.collection[BSONCollection](appColl)

  def probeDatabase(): Future[_] = {
    Logger.info("Probing the database")
    import reactivemongo.bson._
    val query = BSONDocument("alive" -> "OK")
    collection.find(query).one[BSONDocument].map { maybeDoc =>
      maybeDoc.foreach(doc => Logger.info("Database: " + doc.getAs[String]("alive").getOrElse("not OK")))
    }
  }

  probeDatabase()
}
