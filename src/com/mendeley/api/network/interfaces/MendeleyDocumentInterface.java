package com.mendeley.api.network.interfaces;

import java.util.List;
import java.util.Map;

import com.mendeley.api.exceptions.MendeleyException;
import com.mendeley.api.model.Document;
import com.mendeley.api.params.Page;

/**
 * Interface that should be implemented by the application for receiving callbacks from document network calls.
 *
 */
public interface MendeleyDocumentInterface extends MendeleyInterface {

	public void onDocumentsReceived(List<Document> documents, Page next);
	public void onDocumentsNotReceived(MendeleyException mendeleyException);
	
	public void onDocumentReceived(Document document);
	public void onDocumentNotReceived(MendeleyException mendeleyException);
	
	public void onDocumentPosted(Document document);
	public void onDocumentNotPosted(MendeleyException mendeleyException);
	
	public void onDocumentTrashed(String documentId);
	public void onDocumentNotTrashed(MendeleyException mendeleyException);
	
	public void onDocumentDeleted(String documentId);
	public void onDocumentNotDeleted(MendeleyException mendeleyException);
	
	public void onDocumentPatched(String documentId);
	public void onDocumentNotPatched(MendeleyException mendeleyException);
	
	public void onDocumentTypesReceived(Map<String, String> typesMap);
	public void onDocumentTypesNotReceived(MendeleyException mendeleyException);
}
