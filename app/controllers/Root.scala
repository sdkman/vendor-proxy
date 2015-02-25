package controllers

import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

object Root extends Controller {

  case class Success(status: Int, message: String)

  implicit val reads = Json.writes[Success]

  val index = Action {
    Ok(Json.toJson(Success(200, "Service alive")))
  }

}
