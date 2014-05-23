package com.mendeley.api.model;

public class File {
	
	public String id;
	public String documentId;
	public String mimeType;
	public String fileName;
	public String fileHash;
	
	public File() {
		
	}
	
	public File (String id, String documentId, String mimeType, String fileName, String fileHash) {
		this.id = id;
		this.documentId = documentId;
		this.mimeType = mimeType;
		this.fileName = fileName;
		this.fileHash = fileHash;
	}
	
	@Override
	public String toString() {
		return "id: " + id + 
				"documentId: " + documentId + 
				"mimeType: " + mimeType + 
				"fileName: " + fileName + 
				"fileHash: " + fileHash;
	}
}
