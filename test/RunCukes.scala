import io.cucumber.junit.CucumberOptions
import io.cucumber.junit.Cucumber
import org.junit.runner.RunWith

@RunWith(classOf[Cucumber])
@CucumberOptions(
  glue = Array("steps"),
  plugin = Array("html:target/report.html", "pretty")
)
class RunCukes
