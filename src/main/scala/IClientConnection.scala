import scala.scalajs.js
import scala.scalajs.js.Promise
import scala.scalajs.js.annotation._

@js.native
class IdWrapper extends js.Any {
	var id: String = js.native;
}

@ScalaJSDefined
trait IClientConnection extends js.Object {
	/**
	  * Stops the server.
	  */
	def stop();
	
	/**
	  * Adds a listener for validation report coming from the server.
	  * @param listener
	  */
	def onValidationReport(listener: js.Function1[js.Any, js.Any]);
	
	/**
	  * Instead of calling getStructure to get immediate structure report for the document,
	  * this method allows to launch to the new structure reports when those are available.
	  * @param listener
	  */
	def onStructureReport(listener: js.Function1[js.Any, js.Any]);
	
	/**
	  * Notifies the server that document is opened.
	  * @param document
	  */
	def documentOpened(document: js.Any);
	
	/**
	  * Notified the server that document is closed.
	  * @param uri
	  */
	def documentClosed(uri: String);
	
	/**
	  * Notifies the server that document is changed.
	  * @param document
	  */
	def documentChanged(document: js.Any);
	
	/**
	  * Requests server for the document structure.
	  * @param uri
	  */
	def getStructure(uri: String): Promise[js.Any];
	
	/**
	  * Requests server for the suggestions.
	  * @param uri - document uri
	  * @param position - offset in the document, starting from 0
	  */
	def getSuggestions(uri: String, position: Int): js.Promise[js.Array[js.Any]];
	
	/**
	  * Requests server for the positions of the declaration of the element defined
	  * at the given document position.
	  * @param uri - document uri
	  * @param position - position in the document
	  */
	def openDeclaration(uri: String, position: Int): js.Promise[js.Array[js.Any]];
	
	/**
	  * Requests server for the positions of the references of the element defined
	  * at the given document position.
	  * @param uri - document uri
	  * @param position - position in the document
	  */
	def findReferences(uri: String, position: Int): js.Promise[js.Array[js.Any]];
	
	/**
	  * Requests server for the occurrences of the element defined
	  * at the given document position.
	  * @param uri - document uri
	  * @param position - position in the document
	  */
	def markOccurrences(uri: String, position: Int): js.Promise[js.Array[js.Any]];
	
	/**
	  * Requests server for rename of the element
	  * at the given document position.
	  * @param uri - document uri
	  * @param position - position in the document
	  */
	def rename(uri: String, position: Int, newName: String): js.Promise[js.Array[js.Any]];
	
	/**
	  * Gets latest document version.
	  * @param uri
	  */
	def getLatestVersion(uri: String): js.Promise[js.Any];
	
	/**
	  * Listens to the server requests for FS path existence, answering whether
	  * a particular path exists on FS.
	  */
	def onExists(listener: js.Function1[String, js.Promise[Boolean]]);
	
	/**
	  * Listens to the server requests for directory contents, answering with a list
	  * of files in a directory.
	  */
	def onReadDir(listener: js.Function1[String, js.Promise[js.Array[String]]]);
	
	/**
	  * Listens to the server requests for directory check, answering whether
	  * a particular path is a directory.
	  */
	def onIsDirectory(listener: js.Function1[String, js.Promise[Boolean]]);
	
	/**
	  * Listens to the server requests for file contents, answering what contents file has.
	  */
	def onContent(listener: js.Function1[String, js.Promise[String]]);
	
	/**
	  * Requests server for the document+position details.
	  * @param uri
	  */
	def getDetails(uri: String, position: Int): js.Promise[js.Any];
	
	/**
	  * Reports to the server the position (cursor) change on the client.
	  * @param uri - document uri.
	  * @param position - curtsor position, starting from 0.
	  */
	def positionChanged(uri: String, position: Int);
	
	/**
	  * Report from the server that the new details are calculated
	  * for particular document and position.
	  * @param listener
	  */
	def onDetailsReport(listener: js.Function1[js.Any, js.Any]);
	
	/**
	  * Executes the specified details action.
	  * @param uri - document uri
	  * @param actionID - ID of the action to execute.
	  * @param position - optional position in the document.
	  * If not provided, the last reported by positionChanged method will be used.
	  */
	def executeDetailsAction(uri: String, actionID: String, position: Int): js.Promise[js.Array[js.Any]];
	
	/**
	  * Calculates the list of executable actions avilable in the current context.
	  *
	  * @param uri - document uri.
	  * @param position - optional position in the document.
	  * If not provided, the last reported by positionChanged method will be used.
	  */
	def calculateEditorContextActions(uri: String, position: Int): js.Promise[js.Array[js.Any]];
	
	/**
	  * Calculates the list of all available actions.
	  */
	def allAvailableActions(): js.Promise[js.Array[js.Any]];
	
	/**
	  * Executes the specified action. If action has UI, causes a consequent
	  * server->client UI message resulting in onDisplayActionUI listener call.
	  * @param uri - document uri
	  * @param action - action to execute.
	  * @param position - optional position in the document.
	  * If not provided, the last reported by positionChanged method will be used.
	  */
	def executeContextAction(uri: String, action: IdWrapper, position: Int): js.Promise[js.Array[js.Any]];
	
	/**
	  * Executes the specified action. If action has UI, causes a consequent
	  * server->client UI message resulting in onDisplayActionUI listener call.
	  * @param uri - document uri
	  * @param actionID - actionID to execute.
	  * @param position - optional position in the document.
	  * If not provided, the last reported by positionChanged method will be used.
	  */
	def executeContextActionByID(uri: String, actionID: String, position: Int): js.Promise[js.Array[js.Any]];
	
	/**
	  * Adds a listener to display action UI.
	  * @param listener - accepts UI display request, should result in a promise
	  * returning final UI state to be transferred to the server.
	  */
	def onDisplayActionUI(listener: js.Function1[js.Any, js.Promise[js.Any]]);
	
	/**
	  * Sets server configuration.
	  * @param serverSettings
	  */
	def setServerConfiguration(serverSettings: js.Any);
	
	/**
	  * Changes value of details item.
	  * @param uri
	  * @param position
	  * @param itemID
	  * @param value
	  */
	def changeDetailValue(uri: String, position: Int, itemID: String, value: js.Any): js.Promise[js.Array[js.Any]];
}
