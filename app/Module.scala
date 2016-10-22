import com.google.inject.AbstractModule
import utils.MongoHealthCheck

class Module extends AbstractModule {
  def configure() = bind(classOf[MongoHealthCheck]).asEagerSingleton()
}
