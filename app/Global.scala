import play.api._
import play.modules.reactivemongo.ReactiveMongoApi
import utils.HealthCheckCapability

object Global extends GlobalSettings with HealthCheckCapability {

  import Play.current
  lazy val reactiveMongoApi = current.injector.instanceOf[ReactiveMongoApi]


  override def onStart(app: Application) {
    Logger.info("Application has started...")
    probeDatabase()
  }
}