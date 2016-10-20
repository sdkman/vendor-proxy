package controllers

import com.google.inject.Inject
import play.api.libs.json.Json.obj
import play.api.mvc._
import play.modules.reactivemongo.ReactiveMongoApi
import utils.{Environment, HealthCheckCapability}

import scala.concurrent.ExecutionContext.Implicits.global

class Health @Inject() (val reactiveMongoApi: ReactiveMongoApi) extends Controller with HealthCheckCapability {

  val alive = Action.async { request =>
    probeDatabase().map(s => Ok(obj("status" -> 200, "alive" -> true))).recover {
      case e: Exception => ServiceUnavailable(obj("status" -> 503, "alive" -> false, "message" -> e.getMessage))
    }
  }

  val info = Action { request =>
    Ok(obj("version" -> Environment.version))
  }
}
