package com.mendeley.api.model;

/**
 * Model class representing file json object.
 *
 */
public class File {
	
	public String id;
	public String documentId;
	public String mimeType;
	public String fileName;
	public String fileHash;
	public String fileSystemName;
	
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
				", documentId: " + documentId + 
				", mimeType: " + mimeType + 
				", fileName: " + fileName + 
				", fileHash: " + fileHash;
	}
	
	@Override
	public boolean equals(Object object) {
		
		File other;
		
		try {
			other = (File) object;
		}
		catch (ClassCastException e) {
			return false;
		}
		
		if (other == null) {
			return false;
		} else {
			return other.id.equals(this.id);
		}
	}
}
