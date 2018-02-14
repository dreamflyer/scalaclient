import scala.scalajs.js
import js.annotation._

@js.native
class JSTextEdit extends js.Any {
	var range: JSRange = js.native;
	
	var text: String = js.native;
}

@js.native
class JSOpenedDocument extends js.Any {
	var uri: String = js.native;
	
	var text: String = js.native;
}

@js.native
class JSChangedDocument extends js.Any {
	var uri: String = js.native;
	
	var text: String = js.native;
	
	var textEdits: js.Array[JSTextEdit] = js.native;
}

@js.native
class JSRange extends js.Any {
	var start: Int = js.native;
	var end: Int = js.native;
}

@ScalaJSDefined
class ScalaOpenedDocument extends js.Object {
	var uri: String = null;
	
	var text: String = null;
	
	var version: Int = 0;
}

@ScalaJSDefined
class VersionedDocument extends js.Object {
	private var uri: String = null;
	
	private var text: String = null;
	
	private var version: Int = 0;
	
	def init(uri: String, version: Int, text: String): VersionedDocument = {
		this.uri = uri;
		this.text = text;
		this.version = version;
		
		return this;
	}
	
	def getText(): String = {
		this.text;
	}
	
	def getUri() = {
		this.uri;
	}
	
	def getVersion(): Int = {
		this.version;
	}
}

@ScalaJSDefined
class VersionedDocumentManager extends js.Object {
	private var documents: scala.collection.mutable.Map[String, scala.collection.mutable.MutableList[VersionedDocument]] = scala.collection.mutable.Map[String, scala.collection.mutable.MutableList[VersionedDocument]]();
	
	def getLatestDocument(uri: String): VersionedDocument = {
		var versionedDocuments = this.documents.get(uri).orNull;
		
		if(versionedDocuments == null) {
			return null;
		}
		
		versionedDocuments.get(0).orNull;
	}
	
	def getLatestDocumentVersion(uri: String): Int = {
		var latestDocument = this.getLatestDocument(uri);
		
		if(latestDocument == null) {
			return -1;
		}
		
		latestDocument.getVersion();
	}
	
	def registerOpenedDocument(proposal: JSOpenedDocument): ScalaOpenedDocument = {
		var versionedDocuments = this.documents.get(proposal.uri).orNull;
		
		if(versionedDocuments == null) {
			var newDocument = new VersionedDocument().init(proposal.uri, 0, proposal.text);
			
			versionedDocuments = new scala.collection.mutable.MutableList[VersionedDocument]();
			
			versionedDocuments += newDocument;
			
			this.documents += (proposal.uri -> versionedDocuments);
		}
		
		var result = new ScalaOpenedDocument();
		
		result.uri = proposal.uri;
		result.text = proposal.text;
		result.version = 0;
		
		result;
	}
	
	def registerChangedDocument(proposal: JSChangedDocument): VersionedDocument = {
		var versionedDocuments = this.documents.get(proposal.uri).orNull;
		
		if (versionedDocuments != null) {
			var latestDocument = versionedDocuments.get(0).orElse(null).orNull;
			
			var latestText = latestDocument.getText();
			
			var newText = proposal.text;
			
			if(Util.orNull(newText) == null && Util.orNull(proposal.textEdits) != null && Util.orNull(latestText) != null) {
				newText = applyDocumentEdits(latestText, proposal.textEdits);
			}
			
			if(Util.orNull(newText) == null) {
				return null;
			}
			
			if(newText == latestText) {
				return null;
			}
			
			var newDocument = new VersionedDocument().init(proposal.uri, latestDocument.getVersion() + 1, newText);
			
			var jsArray = new scala.collection.mutable.MutableList[VersionedDocument]();
			
			jsArray += newDocument;
			
			this.documents += (proposal.uri -> jsArray);
			
			new VersionedDocument().init(newDocument.getUri(), newDocument.getVersion(), newDocument.getText());
		} else {
			var newDocument = new VersionedDocument().init(proposal.uri, 0, proposal.text);
			
			var jsArray = new scala.collection.mutable.MutableList[VersionedDocument]();
			
			jsArray += newDocument;
			
			this.documents += (proposal.uri -> jsArray);
			
			new VersionedDocument().init(proposal.uri, 0, proposal.text);
		}
	}
	
	def applyDocumentEdit(oldContents: String, edit: JSTextEdit): String = {
		if (edit.range.end == 0) {
			return edit.text + oldContents;
		}
		
		if(edit.range.start >= oldContents.length) {
			return oldContents + edit.text;
		}
		
		if(edit.range.start < 0 || edit.range.end > oldContents.length) {
			throw new Error("Range of [" + edit.range.start + ":" + edit.range.end + "] is not applicable to document of length " + oldContents.length);
		}
		
		if(edit.range.start >= edit.range.end) {
			throw new Error("Range of [" + edit.range.start + ":" + edit.range.end + "] should have end greater than start");
		}
		
		var beginning = "";
		
		if(edit.range.start > 0) {
			beginning = oldContents.substring(0, edit.range.start - 1);
		}
		
		var end = "";
		
		if (edit.range.end < oldContents.length) {
			end = oldContents.substring(edit.range.end);
		}
		
		return beginning + edit.text + end;
	}
	
	def applyDocumentEdits(oldContents: String, edits: js.Array[JSTextEdit]): String = {
		if(edits.length > 1) {
			throw new Error("Unsupported application of more than 1 text editor at once to a single file");
		}
		
		applyDocumentEdit(oldContents, edits.apply(0));
	}
	
	def unregisterDocument(uri: String) {
		this.documents.remove(uri);
	}
}
