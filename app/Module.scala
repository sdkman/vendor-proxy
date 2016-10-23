import com.google.inject.AbstractModule
import repos.HealthCheckRepo

class Module extends AbstractModule {
  def configure() = bind(classOf[HealthCheckRepo]).asEagerSingleton()
}
