package com.mendeley.api.network.interfaces;

import java.util.List;

import com.mendeley.api.exceptions.MendeleyException;
import com.mendeley.api.model.File;
import com.mendeley.api.network.components.Paging;

/**
 * Interface that should be implemented by the application for receiving callbacks from file network calls.
 *
 */
public interface MendeleyFileInterface extends MendeleyInterface {

	public void onFilesReceived(List<File> files, Paging paging);
	public void onFilesNotReceived(MendeleyException mendeleyException);
	
	public void onFileReceived(String fileName, String fileId, Paging paging);
	public void onFileNotReceived(MendeleyException mendeleyException);
	
	public void onFilePosted(File file, Paging paging);
	public void onFileNotPosted(MendeleyException mendeleyException);
	
	public void onFileDeleted(String fileId, Paging paging);
	public void onFileNotDeleted(MendeleyException mendeleyException);

}
