package controllers

import play.api.libs.json.Json
import play.api.mvc._
import utils.Environment

object Health extends Controller with Environment {

  val alive = Action { request =>
    Ok(Json.obj("status" -> 200, "message" -> "Alive!"))
  }

  val info = Action { request =>
    Ok(Json.obj("version" -> version))
  }
}
