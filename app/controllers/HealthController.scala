package controllers

import com.google.inject.Inject
import play.api.libs.json.Json.obj
import play.api.mvc._
import utils.{ErrorMarshalling, VendorProxyConfig}

class HealthController @Inject() (
    val config: VendorProxyConfig,
    val cc: ControllerComponents
) extends AbstractController(cc)
    with ErrorMarshalling {

  val alive = Action {
    Ok(obj("status" -> 200, "alive" -> true, "message" -> "healthy"))
  }

  val info = Action { request =>
    Ok(obj("version" -> config.version))
  }
}
