import scala.scalajs.js
import js.annotation.ScalaJSDefined

@ScalaJSDefined
trait ILogger extends js.Object {
	def log(message: js.Any, severity: js.Any, component: js.Any, subcomponent: js.Any);
	
	def debug(message: js.Any, severity: js.Any, component: js.Any, subcomponent: js.Any);
	
	def debugDetail(message: js.Any, severity: js.Any, component: js.Any, subcomponent: js.Any);
	
	def debugOverview(message: js.Any, severity: js.Any, component: js.Any, subcomponent: js.Any);
	
	def warning(message: js.Any, severity: js.Any, component: js.Any, subcomponent: js.Any);
	
	def error(message: js.Any, severity: js.Any, component: js.Any, subcomponent: js.Any);
	
	def setLoggerConfiguration(loggerSettings: js.Any);
}