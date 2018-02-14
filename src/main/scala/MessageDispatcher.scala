import scala.concurrent._
import scala.util.{Success, Failure}

import ExecutionContext.Implicits.global

import scalajs.js

import js.annotation._
import js.JSConverters._

@js.native
@JSImport("shortid", JSImport.Namespace)
object Shortid extends js.Any {
	def generate(): String = js.native
}

@ScalaJSDefined
class ProtocolMessage extends js.Object {
	@JSName("type")
	var typeName: String = null;
	
	var payload: js.Any = null;
	
	var id: String = null;
	
	var errorMessage: String = null;
	
	def fromJs(jsMessage: JSProtocolMessage): ProtocolMessage = {
		this.id = Util.orNull(jsMessage.id);
		this.typeName = Util.orNull(jsMessage.typeName);
		this.payload = Util.orNull(jsMessage.payload);
		this.errorMessage = Util.orNull(jsMessage.errorMessage);
		
		this;
	}
}

@js.native
class JSProtocolMessage extends js.Any {
	@JSName("type")
	var typeName: js.Any = js.native;
	
	var payload: js.Any = js.native;
	
	var id: js.Any = js.native;
	
	var errorMessage: js.Any = js.native;
}

class ResolveReject {
	var resolve: Function1[Any, Any] = null;
	
	var reject: Function1[Throwable, Any] = null;
}

@ScalaJSDefined
abstract class MessageDispatcher extends js.Object with ILogger {
	var callbacks: scala.collection.mutable.Map[String, ResolveReject] = scala.collection.mutable.Map[String, ResolveReject]();
	
	protected def sendMessage(message: ProtocolMessage);
	
	protected def getResponseHandler(name: String): Function1[js.Any, js.Any];
	
	def send(message: ProtocolMessage) {
		this.sendMessage(message);
	}
	
	def sendWithResponse(message: ProtocolMessage): js.Promise[Any] = {
		var id = Shortid.generate();
		
		var callback = new ResolveReject();
		
		var p = Promise[Any]();
		
		message.id = id;
		
		callback.resolve = (msg: Any) => p.success(msg);
		callback.reject = (msg: Throwable) => p.failure(msg);
		
		callbacks += (id -> callback);
		
		this.send(message);
		
		p.future.toJSPromise;
	}
	
	def getCallback(name: String): ResolveReject = {
		if(name == null) {
			return null;
		}
		
		var result = callbacks.get(name).orNull;
		
		if(result != null) {
			callbacks.remove(name);
		}
		
		result;
	}
	
	def handleRecievedMessage(jsMessage: JSProtocolMessage) {
		var message = new ProtocolMessage().fromJs(jsMessage);
		
		var callBackHandle = getCallback(message.id);
		
		if(callBackHandle != null) {
			var errorMessage = message.errorMessage;
			
			if(errorMessage == null) {
				callBackHandle.resolve(message.payload);
			} else {
				callBackHandle.reject(new Exception(message.errorMessage));
			}
		} else {
			var handler = this.getResponseHandler(message.typeName);
			
			if(handler == null) {
				return;
			}
			
			var result: js.Any = null;
			
			try {
				result = Util.orNull(handler(message.payload));
			} catch {
				case error: Throwable => handleCommunicationError(error, message);
				
				return;
			}
			
			if(result != null && Util.orNull(message.id) != null) {
				var responseMessage = new ProtocolMessage();
				
				responseMessage.id = message.id;
				responseMessage.typeName = message.typeName;
				
				if(result.isInstanceOf[js.Promise[js.Any]]) {
					var asPromise = result.asInstanceOf[js.Promise[js.Any]];
					
					var future = asPromise.toFuture;
					
					future.onComplete {
						case Success(resolvedResult) => {
							responseMessage.payload = resolvedResult;
						}
						
						case Failure(error) => {
							responseMessage.payload = new js.Object();
							responseMessage.errorMessage = error.getMessage;
						}
					}
				} else {
					responseMessage.payload = result;
					
					this.sendMessage(responseMessage);
				}
			}
		}
		
	}
	
	protected def handleCommunicationError(error: Throwable, originalMessage: ProtocolMessage) {
		if (Util.orNull(originalMessage.id) == null) {
			return;
		}
		
		var message = new ProtocolMessage();
		
		message.id = originalMessage.id;
		message.typeName = originalMessage.typeName;
		message.payload = new js.Object();
		message.errorMessage = error.getMessage;
		
		this.send(message);
	}
	
	def log(message: js.Any, severity: js.Any, component: js.Any, subcomponent: js.Any) {
	
	}
	
	def debug(message: js.Any, severity: js.Any, component: js.Any, subcomponent: js.Any) {
	
	}
	
	def debugDetail(message: js.Any, severity: js.Any, component: js.Any, subcomponent: js.Any) {
	
	}
	
	def debugOverview(message: js.Any, severity: js.Any, component: js.Any, subcomponent: js.Any) {
	
	}
	
	def warning(message: js.Any, severity: js.Any, component: js.Any, subcomponent: js.Any) {
	
	}
	
	def error(message: js.Any, severity: js.Any, component: js.Any, subcomponent: js.Any) {
	
	}
	
	def setLoggerConfiguration(loggerSettings: js.Any) {
	
	}
}