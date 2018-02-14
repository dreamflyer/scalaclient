import scala.concurrent._

import ExecutionContext.Implicits.global

import scala.scalajs.js
import js.annotation._
import js.JSConverters._

@js.native
class Process extends js.Any {
	def on(eventName: String, callback: js.Function1[JSProtocolMessage, Unit]) = js.native;
	
	def send(message: js.Any) = js.native;
	
	def kill() = js.native;
}

@js.native
@JSImport("child_process", JSImport.Namespace)
object ChildProcessModule extends js.Any {
	def fork(path: String, args: js.Array[String]): Process = js.native
}

object Exposer {
	private var connection: ClientConnection = null;
	
	private var serverPath: String = "";
	
	@JSExportTopLevel("setServerPath")
	def setServerPath(path: String) {
		serverPath = path;
	}
	
	@JSExportTopLevel("getConnection")
	def getConnection(): IClientConnection = {
		if(connection == null) {
			val process = ChildProcessModule.fork(this.serverPath, new js.Array());
			
			connection = new ClientConnection();
			
			connection.init(process);
		}
		
		connection;
	}
}

@ScalaJSDefined
class UriPositionPayload extends js.Object {
	var uri: String = null;
	
	var position: Int = 0;
}

@ScalaJSDefined
class RenamePayload extends UriPositionPayload {
	var newName: String = null;
}

@ScalaJSDefined
class ActionPayload extends UriPositionPayload {
	var actionId: String = null;
}

@ScalaJSDefined
class DetailValuePayload extends UriPositionPayload {
	var itemID: String = null;
	
	var value: js.Any = null;
}

@ScalaJSDefined
class ClientConnection extends MessageDispatcher with IClientConnection {
	private val validationReportListeners: scala.collection.mutable.MutableList[js.Function1[js.Any, js.Any]] = scala.collection.mutable.MutableList[js.Function1[js.Any, js.Any]]();
	private val structureReportListeners: scala.collection.mutable.MutableList[js.Function1[js.Any, js.Any]] = scala.collection.mutable.MutableList[js.Function1[js.Any, js.Any]]();
	private val onExistsListeners: scala.collection.mutable.MutableList[js.Function1[String, js.Promise[Boolean]]] = scala.collection.mutable.MutableList[js.Function1[String, js.Promise[Boolean]]]();
	private val onReadDirListeners: scala.collection.mutable.MutableList[js.Function1[String, js.Promise[js.Array[String]]]] = scala.collection.mutable.MutableList[js.Function1[String, js.Promise[js.Array[String]]]]();
	private val onIsDirectoryListeners: scala.collection.mutable.MutableList[js.Function1[String, js.Promise[Boolean]]] = scala.collection.mutable.MutableList[js.Function1[String, js.Promise[Boolean]]]();
	private val onContentListeners: scala.collection.mutable.MutableList[js.Function1[String, js.Promise[String]]] = scala.collection.mutable.MutableList[js.Function1[String, js.Promise[String]]]();
	private val onDetailsReportListeners: scala.collection.mutable.MutableList[js.Function1[js.Any, js.Any]] = scala.collection.mutable.MutableList[js.Function1[js.Any, js.Any]]();
	private val onDisplayActionUIListeners: scala.collection.mutable.MutableList[js.Function1[js.Any, js.Promise[js.Any]]] = scala.collection.mutable.MutableList[js.Function1[js.Any, js.Promise[js.Any]]]();
	
	private val handlers: scala.collection.mutable.Map[String, Function1[js.Any, js.Any]] = scala.collection.mutable.Map[String, Function1[js.Any, js.Any]]();
	
	private var serverProcess: Process = null;
	
	private val versionManager: VersionedDocumentManager = new VersionedDocumentManager();
	
	def init(serverProcess: Process) {
		registerHandlers();
		
		serverProcess.on("message", (message: JSProtocolMessage) => {
			this.handleRecievedMessage(message);
		});
		
		this.serverProcess = serverProcess;
	}
	
	def sendMessage(message: ProtocolMessage) {
		serverProcess.send(message);
	}
	
	def getResponseHandler(name: String): Function1[js.Any, js.Any] = {
		handlers.get(name).orNull
	}
	
	private def registerHandlers() {
		this.handlers += ("VALIDATION_REPORT" -> ((arg: js.Any) => this.VALIDATION_REPORT(arg.asInstanceOf[js.Any]).asInstanceOf[js.Any]));
		this.handlers += ("STRUCTURE_REPORT" -> ((arg: js.Any) => this.STRUCTURE_REPORT(arg.asInstanceOf[js.Any]).asInstanceOf[js.Any]));
		this.handlers += ("EXISTS" -> ((arg: js.Any) => this.EXISTS(arg.asInstanceOf[String]).asInstanceOf[js.Any]));
		this.handlers += ("READ_DIR" -> ((arg: js.Any) => this.READ_DIR(arg.asInstanceOf[String]).asInstanceOf[js.Any]));
		this.handlers += ("IS_DIRECTORY" -> ((arg: js.Any) => this.IS_DIRECTORY(arg.asInstanceOf[String]).asInstanceOf[js.Any]));
		this.handlers += ("CONTENT" -> ((arg: js.Any) => this.CONTENT(arg.asInstanceOf[String]).asInstanceOf[js.Any]));
		this.handlers += ("DETAILS_REPORT" -> ((arg: js.Any) => this.DETAILS_REPORT(arg.asInstanceOf[js.Any]).asInstanceOf[js.Any]));
		this.handlers += ("DISPLAY_ACTION_UI" -> ((arg: js.Any) => this.DISPLAY_ACTION_UI(arg.asInstanceOf[js.Any]).asInstanceOf[js.Any]));
	}
	
	def setServerConfiguration(serverSettings: js.Any) {
		var message = new ProtocolMessage();
		
		message.typeName = "SET_SERVER_CONFIGURATION";
		message.payload = serverSettings;
		
		this.send(message);
	}
	
	def stop() {
		this.serverProcess.kill();
	}
	
	def onValidationReport(listener: js.Function1[js.Any, js.Any]) {
		this.validationReportListeners += listener;
	}
	
	def onStructureReport(listener: js.Function1[js.Any, js.Any]) {
		this.structureReportListeners += listener;
	}
	
	def onDetailsReport(listener: js.Function1[js.Any, js.Any]) {
		this.onDetailsReportListeners += listener;
	}
	
	def onExists(listener: js.Function1[String, js.Promise[Boolean]]) {
		this.onExistsListeners += listener;
	}
	
	def onReadDir(listener: js.Function1[String, js.Promise[js.Array[String]]]) {
		this.onReadDirListeners += listener;
	}
	
	def onIsDirectory(listener: js.Function1[String, js.Promise[Boolean]]) {
		this.onIsDirectoryListeners += listener;
	}
	
	def onContent(listener: js.Function1[String, js.Promise[String]]) {
		this.onContentListeners += listener;
	}
	
	def onDisplayActionUI(listener: js.Function1[js.Any, js.Promise[js.Any]]) {
		this.onDisplayActionUIListeners += listener;
	}
	
	def documentOpened(document: js.Any) {
		var commonOpenedDocument = this.versionManager.registerOpenedDocument(document.asInstanceOf[JSOpenedDocument]);
		
		if(Util.orNull(commonOpenedDocument) == null) {
			return;
		}
		
		var message = new ProtocolMessage();
		
		message.typeName = "OPEN_DOCUMENT";
		message.payload = commonOpenedDocument;
		
		this.send(message);
	}
	
	def documentChanged(document: js.Any) {
		var commonChangedDocument = this.versionManager.registerChangedDocument(document.asInstanceOf[JSChangedDocument]);
		
		if(Util.orNull(commonChangedDocument) == null) {
			return;
		}
		
		var message = new ProtocolMessage();
		
		message.typeName = "CHANGE_DOCUMENT";
		message.payload = commonChangedDocument;
		
		this.send(message);
	}
	
	def documentClosed(uri: String) {
		var message = new ProtocolMessage();
		
		message.typeName = "CLOSE_DOCUMENT";
		message.payload = uri;
		
		this.send(message);
	}
	
	def getStructure(uri: String): js.Promise[js.Any] = {
		var message = new ProtocolMessage();
		
		message.typeName = "GET_STRUCTURE";
		message.payload = uri;
		
		this.sendWithResponse(message).asInstanceOf[js.Promise[js.Any]];
	}
	
	def getSuggestions(uri: String, position: Int): js.Promise[js.Array[js.Any]] = {
		var message = new ProtocolMessage();
		
		var payload = new UriPositionPayload();
		
		payload.uri = uri;
		payload.position = position;
		
		message.typeName = "GET_SUGGESTIONS";
		message.payload = payload;
		
		this.sendWithResponse(message).asInstanceOf[js.Promise[js.Array[js.Any]]];
	}
	
	def openDeclaration(uri: String, position: Int): js.Promise[js.Array[js.Any]] = {
		var message = new ProtocolMessage();
		
		var payload = new UriPositionPayload();
		
		payload.uri = uri;
		payload.position = position;
		
		message.typeName = "OPEN_DECLARATION";
		message.payload = payload;
		
		this.sendWithResponse(message).asInstanceOf[js.Promise[js.Array[js.Any]]];
	}
	
	def findReferences(uri: String, position: Int): js.Promise[js.Array[js.Any]] = {
		var message = new ProtocolMessage();
		
		var payload = new UriPositionPayload();
		
		payload.uri = uri;
		payload.position = position;
		
		message.typeName = "FIND_REFERENCES";
		message.payload = payload;
		
		this.sendWithResponse(message).asInstanceOf[js.Promise[js.Array[js.Any]]];
	}
	
	def markOccurrences(uri: String, position: Int): js.Promise[js.Array[js.Any]] = {
		var message = new ProtocolMessage();
		
		var payload = new UriPositionPayload();
		
		payload.uri = uri;
		payload.position = position;
		
		message.typeName = "MARK_OCCURRENCES";
		message.payload = payload;
		
		this.sendWithResponse(message).asInstanceOf[js.Promise[js.Array[js.Any]]];
	}
	
	def rename(uri: String, position: Int, newName: String): js.Promise[js.Array[js.Any]] = {
		var message = new ProtocolMessage();
		
		var payload = new RenamePayload();
		
		payload.uri = uri;
		payload.position = position;
		payload.newName = newName;
		
		message.typeName = "RENAME";
		message.payload = payload;
		
		this.sendWithResponse(message).asInstanceOf[js.Promise[js.Array[js.Any]]];
	}
	
	def getDetails(uri: String, position: Int): js.Promise[js.Any] = {
		var message = new ProtocolMessage();
		
		var payload = new UriPositionPayload();
		
		payload.uri = uri;
		payload.position = position;
		
		message.typeName = "GET_DETAILS";
		message.payload = payload;
		
		this.sendWithResponse(message).asInstanceOf[js.Promise[js.Any]];
	}
	
	def getLatestVersion(uri: String): js.Promise[js.Any] = {
		var version = this.versionManager.getLatestDocumentVersion(uri);
		
		var p = Promise[js.Any]();
		
		if(version >= 0) {
			p.success(version);
		} else {
			p.success(null);
		}
		
		p.future.toJSPromise;
	}
	
	def positionChanged(uri: String, position: Int) {
		var message = new ProtocolMessage();
		
		var payload = new UriPositionPayload();
		
		payload.uri = uri;
		payload.position = position;
		
		message.typeName = "CHANGE_POSITION";
		message.payload = payload;
		
		this.send(message);
	}
	
	def executeDetailsAction(uri: String, actionID: String, position: Int): js.Promise[js.Array[js.Any]] = {
		var message = new ProtocolMessage();
		
		var payload = new ActionPayload();
		
		payload.uri = uri;
		payload.position = position;
		payload.actionId = actionID;
		
		message.typeName = "EXECUTE_DETAILS_ACTION";
		message.payload = payload;
		
		this.sendWithResponse(message).asInstanceOf[js.Promise[js.Array[js.Any]]];
	}
	
	def calculateEditorContextActions(uri: String, position: Int): js.Promise[js.Array[js.Any]] = {
		var message = new ProtocolMessage();
		
		var payload = new UriPositionPayload();
		
		payload.uri = uri;
		payload.position = position;
		
		message.typeName = "CALCULATE_ACTIONS";
		message.payload = payload;
		
		this.sendWithResponse(message).asInstanceOf[js.Promise[js.Array[js.Any]]];
	}
	
	def allAvailableActions(): js.Promise[js.Array[js.Any]] = {
		var message = new ProtocolMessage();
		
		message.typeName = "ALL_ACTIONS";
		message.payload = new js.Object();
		
		this.sendWithResponse(message).asInstanceOf[js.Promise[js.Array[js.Any]]];
	}
	
	def executeContextAction(uri: String, action: IdWrapper, position: Int): js.Promise[js.Array[js.Any]] = {
		var message = new ProtocolMessage();
		
		var payload = new ActionPayload();
		
		payload.uri = uri;
		payload.position = position;
		payload.actionId = action.id;
		
		message.typeName = "EXECUTE_ACTION";
		message.payload = payload;
		
		this.sendWithResponse(message).asInstanceOf[js.Promise[js.Array[js.Any]]];
	}
	
	def executeContextActionByID(uri: String, actionID: String, position: Int): js.Promise[js.Array[js.Any]] = {
		var message = new ProtocolMessage();
		
		var payload = new ActionPayload();
		
		payload.uri = uri;
		payload.position = position;
		payload.actionId = actionID;
		
		message.typeName = "EXECUTE_ACTION";
		message.payload = payload;
		
		this.sendWithResponse(message).asInstanceOf[js.Promise[js.Array[js.Any]]];
	}
	
	def changeDetailValue(uri: String, position: Int, itemID: String, value: js.Any): js.Promise[js.Array[js.Any]] = {
		var message = new ProtocolMessage();
		
		var payload = new DetailValuePayload();
		
		payload.uri = uri;
		payload.position = position;
		payload.itemID = itemID;
		payload.value = value;
		
		message.typeName = "CHANGE_DETAIL_VALUE";
		message.payload = payload;
		
		this.sendWithResponse(message).asInstanceOf[js.Promise[js.Array[js.Any]]];
	}
	
	def VALIDATION_REPORT(report: js.Any) {
		validationReportListeners.foreach(_(report));
	}
	
	def STRUCTURE_REPORT(report: js.Any) {
		structureReportListeners.foreach(_(report));
	}
	
	def EXISTS(path: String): js.Promise[Boolean] = {
		var result: js.Promise[Boolean] = null;
		
		for(listener <- onExistsListeners) {
			result = listener(path);
		}
		
		result;
	}
	
	def READ_DIR(path: String): js.Promise[js.Array[String]] = {
		var result: js.Promise[js.Array[String]] = null;
		
		for(listener <- onReadDirListeners) {
			result = listener(path);
		}
		
		result;
	}
	
	def IS_DIRECTORY(path: String): js.Promise[Boolean] = {
		var result: js.Promise[Boolean] = null;
		
		for(listener <- onIsDirectoryListeners) {
			result = listener(path);
		}
		
		result;
	}
	
	def CONTENT(path: String): js.Promise[String] = {
		var result: js.Promise[String] = null;
		
		for(listener <- onContentListeners) {
			result = listener(path);
		}
		
		result;
	}
	
	def DETAILS_REPORT(report: js.Any) {
		onDetailsReportListeners.foreach(_(report));
	}
	
	def DISPLAY_ACTION_UI(request: js.Any): js.Promise[js.Any] = {
		var result: js.Promise[js.Any] = null;
		
		for(listener <- onDisplayActionUIListeners) {
			
			result = listener(request);
		}
		
		result;
	}
}