package controllers

import com.google.inject.Inject
import play.api.libs.json.Json.obj
import play.api.mvc._
import repos.HealthCheckRepo
import utils.VendorProxyConfig

import scala.concurrent.ExecutionContext.Implicits.global

class Health @Inject() (val config: VendorProxyConfig, val hcr: HealthCheckRepo) extends Controller {

  val alive = Action.async { request =>
    hcr.probeDatabase().map(s => Ok(obj("status" -> 200, "alive" -> true))).recover {
      case e: Exception => ServiceUnavailable(obj("status" -> 503, "alive" -> false, "message" -> e.getMessage))
    }
  }

  val info = Action { request =>
    Ok(obj("version" -> config.version))
  }
}
