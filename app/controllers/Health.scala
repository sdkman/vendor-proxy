package controllers

import com.google.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import play.api.libs.json.Json.obj
import play.api.mvc._
import slick.driver.JdbcProfile
import utils.{ErrorMarshalling, VendorProxyConfig}

import scala.concurrent.ExecutionContext.Implicits.global

class Health @Inject()(val config: VendorProxyConfig, val dbConfigProvider: DatabaseConfigProvider)
  extends Controller
    with HasDatabaseConfigProvider[JdbcProfile]
    with ErrorMarshalling {

  val alive = Action.async { request =>
    import slick.driver.PostgresDriver.api._
    db.run(sql"SELECT 1".as[(String)]).map { (maybe: Vector[String]) =>
      maybe.headOption.map { result =>
        Ok(obj("status" -> 200, "alive" -> true, "message" -> s"result from test query: $result"))
      }.get
    }.recover {
      case e: Throwable => ServiceUnavailable(serviceUnavailableMsg(e))
    }
  }

  val info = Action { request =>
    Ok(obj("version" -> config.version))
  }
}
