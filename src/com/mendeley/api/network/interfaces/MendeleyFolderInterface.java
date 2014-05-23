package com.mendeley.api.network.interfaces;

import java.util.List;

import com.mendeley.api.exceptions.MendeleyException;
import com.mendeley.api.model.Document;
import com.mendeley.api.model.Folder;

public interface MendeleyFolderInterface extends MendeleyInterface{

	public void onFoldersReceived(List<Folder> folders, MendeleyException exception);
	
	public void onFolderReceived(Folder folder, MendeleyException exception);
		
	public void onFolderDocumentIdsReceived(List<String> documentIds, MendeleyException exception);
	
	public void onFolderPosted(Folder folder, MendeleyException exception);
	
	public void onDocumentPostedToFolder(String folderId, MendeleyException exception);
	
	public void onFolderDeleted(String folderId, MendeleyException exception);
	
	public void onFolderDocumentDeleted(String documentId, MendeleyException exception);
	
	public void onFolderPatched(String folderId, MendeleyException exception);
	
}
