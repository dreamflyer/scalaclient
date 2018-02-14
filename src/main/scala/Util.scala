import scala.scalajs.js
import js.annotation._

@ScalaJSDefined
object Util extends js.Object {
	def orNull[T](value: js.Any): T = {
		if(js.isUndefined(value)) {
			null.asInstanceOf[T];
		} else {
			Some(value).orNull.asInstanceOf[T]
		}
	}
}
