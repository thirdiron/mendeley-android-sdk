package com.mendeley.api.network.interfaces;

import java.util.List;

import com.mendeley.api.exceptions.MendeleyException;
import com.mendeley.api.model.Document;

public interface MendeleyDocumentInterface extends MendeleyInterface {

	public void onDocumentsReceived(List<Document> documents, MendeleyException exception);
	
	public void onDocumentReceived(Document document, MendeleyException exception);
	
	public void onDocumentPosted(Document document, MendeleyException exception);
	
	public void onDocumentTrashed(String documentId, MendeleyException exception);
	
	public void onDocumentDeleted(String documentId, MendeleyException exception);
	
	public void onDocumentPatched(String documentId, MendeleyException exception);
}
