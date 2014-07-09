package com.mendeley.api.network.interfaces;

import java.util.List;

import com.mendeley.api.exceptions.MendeleyException;
import com.mendeley.api.model.DocumentId;
import com.mendeley.api.model.Folder;
import com.mendeley.api.network.components.Page;

/**
 * Interface that should be implemented by the application for receiving callbacks from folder network calls.
 *
 */
public interface MendeleyFolderInterface extends MendeleyInterface{

	public void onFoldersReceived(List<Folder> folders, Page next);
	public void onFoldersNotReceived(MendeleyException mendeleyException);
	
	public void onFolderReceived(Folder folder);
	public void onFolderNotReceived(MendeleyException mendeleyException);
		
	public void onFolderDocumentIdsReceived(String folderId, List<DocumentId> documentIds, Page next);
	public void onFolderDocumentIdsNotReceived(MendeleyException mendeleyException);
	
	public void onFolderPosted(Folder folder);
	public void onFolderNotPosted(MendeleyException mendeleyException);
	
	public void onDocumentPostedToFolder(String folderId);
	public void onDocumentNotPostedToFolder(MendeleyException mendeleyException);
	
	public void onFolderDeleted(String folderId);
	public void onFolderNotDeleted(MendeleyException mendeleyException);
	
	public void onFolderDocumentDeleted(String documentId);
	public void onFolderDocumentNotDeleted(MendeleyException mendeleyException);
	
	public void onFolderPatched(String folderId);
	public void onFolderNotPatched(MendeleyException mendeleyException);
	
}
