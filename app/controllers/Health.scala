package controllers

import com.google.inject.Inject
import play.api.libs.json.Json.obj
import play.api.mvc._
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.bson.BSONDocument
import reactivemongo.play.json.collection.JSONCollection
import utils.Environment

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class Health @Inject() (val reactiveMongoApi: ReactiveMongoApi) extends Controller {

  val alive = Action.async { request =>
    probeDatabase.map(s => Ok(obj("status" -> 200, "alive" -> true))).recover {
      case e: Exception => ServiceUnavailable(obj("status" -> 503, "alive" -> false, "message" -> e.getMessage))
    }
  }

  val info = Action { request =>
    Ok(obj("version" -> Environment.version))
  }

  import play.modules.reactivemongo.json._

  lazy val appColl = Environment.applicationCollection

  lazy val collection = reactiveMongoApi.db.collection[JSONCollection](appColl)

  def probeDatabase: Future[_] = collection.find(BSONDocument("alive" -> "OK")).one[BSONDocument]
}
