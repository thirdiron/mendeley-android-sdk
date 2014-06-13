package com.mendeley.api.network.interfaces;

import java.util.List;

import com.mendeley.api.exceptions.MendeleyException;
import com.mendeley.api.model.Folder;
import com.mendeley.api.network.components.Paging;

/**
 * Interface that should be implemented by the application for receiving callbacks from folder network calls.
 *
 */
public interface MendeleyFolderInterface extends MendeleyInterface{

	public void onFoldersReceived(List<Folder> folders, Paging paging);
	public void onFoldersNotReceived(MendeleyException mendeleyException);
	
	public void onFolderReceived(Folder folder, Paging paging);
	public void onFolderNotReceived(MendeleyException mendeleyException);
		
	public void onFolderDocumentIdsReceived(List<String> documentIds, Paging paging);
	public void onFolderDocumentIdsNotReceived(MendeleyException mendeleyException);
	
	public void onFolderPosted(Folder folder, Paging paging);
	public void onFolderNotPosted(MendeleyException mendeleyException);
	
	public void onDocumentPostedToFolder(String folderId, Paging paging);
	public void onDocumentNotPostedToFolder(MendeleyException mendeleyException);
	
	public void onFolderDeleted(String folderId, Paging paging);
	public void onFolderNotDeleted(MendeleyException mendeleyException);
	
	public void onFolderDocumentDeleted(String documentId, Paging paging);
	public void onFolderDocumentNotDeleted(MendeleyException mendeleyException);
	
	public void onFolderPatched(String folderId, Paging paging);
	public void onFolderNotPatched(MendeleyException mendeleyException);
	
}
