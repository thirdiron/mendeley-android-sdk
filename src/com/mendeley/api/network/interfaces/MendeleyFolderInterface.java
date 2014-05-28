package com.mendeley.api.network.interfaces;

import java.util.List;

import com.mendeley.api.model.Folder;
import com.mendeley.api.network.components.MendeleyResponse;

/**
 * Interface that should be implemented by the application for receiving callbacks from folder network calls.
 *
 */
public interface MendeleyFolderInterface extends MendeleyInterface{

	public void onFoldersReceived(List<Folder> folders, MendeleyResponse mendeleyResponse);
	
	public void onFolderReceived(Folder folder, MendeleyResponse mendeleyResponse);
		
	public void onFolderDocumentIdsReceived(List<String> documentIds, MendeleyResponse mendeleyResponse);
	
	public void onFolderPosted(Folder folder, MendeleyResponse mendeleyResponse);
	
	public void onDocumentPostedToFolder(String folderId, MendeleyResponse mendeleyResponse);
	
	public void onFolderDeleted(String folderId, MendeleyResponse mendeleyResponse);
	
	public void onFolderDocumentDeleted(String documentId, MendeleyResponse mendeleyResponse);
	
	public void onFolderPatched(String folderId, MendeleyResponse mendeleyResponse);
	
}
