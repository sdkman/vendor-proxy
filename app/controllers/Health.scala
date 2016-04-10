package controllers

import play.api.libs.json.Json.obj
import play.api.mvc._
import play.modules.reactivemongo.MongoController
import play.modules.reactivemongo.json.collection.JSONCollection
import reactivemongo.bson.BSONDocument
import utils.Environment

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Health extends Controller with MongoController {

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

  lazy val collection: JSONCollection = db.collection[JSONCollection](appColl)

  def probeDatabase: Future[_] = collection.find(BSONDocument("alive" -> "OK")).one[BSONDocument]
}
