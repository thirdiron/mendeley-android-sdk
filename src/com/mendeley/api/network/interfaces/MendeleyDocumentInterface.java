package com.mendeley.api.network.interfaces;

import java.util.List;

import com.mendeley.api.exceptions.MendeleyException;
import com.mendeley.api.model.Document;
import com.mendeley.api.network.components.MendeleyResponse;

/**
 * Interface that should be implemented by the application for receiving callbacks from document network calls.
 * 
 *
 */
public interface MendeleyDocumentInterface extends MendeleyInterface {

	public void onDocumentsReceived(List<Document> documents, MendeleyResponse mendeleyResponse);
	
	public void onDocumentReceived(Document document, MendeleyResponse mendeleyResponse);
	
	public void onDocumentPosted(Document document, MendeleyResponse mendeleyResponse);
	
	public void onDocumentTrashed(String documentId, MendeleyResponse mendeleyResponse);
	
	public void onDocumentDeleted(String documentId, MendeleyResponse mendeleyResponse);
	
	public void onDocumentPatched(String documentId, MendeleyResponse mendeleyResponse);
}
