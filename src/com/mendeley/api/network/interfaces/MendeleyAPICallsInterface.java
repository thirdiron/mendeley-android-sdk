package com.mendeley.api.network.interfaces;

import java.util.List;

import com.mendeley.api.model.Document;

public interface MendeleyAPICallsInterface {

	public void onDocumentsReceived(List<Document> documents);
	
	public void onDocumentReceived(Document document);
	
	public void onDocumentPosted(Document document);
	
	public void onDocumentTrashed(String documentId);
	
	public void onDocumentDeleted(String documentId);
	
	public void onDocumentPatched(String documentId);
}
