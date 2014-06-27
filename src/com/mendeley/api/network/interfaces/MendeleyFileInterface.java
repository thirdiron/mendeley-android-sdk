package com.mendeley.api.network.interfaces;

import java.util.List;

import com.mendeley.api.exceptions.MendeleyException;
import com.mendeley.api.model.File;
import com.mendeley.api.network.components.Page;

/**
 * Interface that should be implemented by the application for receiving callbacks from file network calls.
 *
 */
public interface MendeleyFileInterface extends MendeleyInterface {

	public void onFilesReceived(List<File> files, Page next);
	public void onFilesNotReceived(MendeleyException mendeleyException);
	
	public void onFileReceived(String fileName, String fileId);
	public void onFileNotReceived(MendeleyException mendeleyException);
	
	public void onFilePosted(File file);
	public void onFileNotPosted(MendeleyException mendeleyException);
	
	public void onFileDownloadProgress(String fileId, int progress);

	public void onFileDeleted(String fileId);
	public void onFileNotDeleted(MendeleyException mendeleyException);


}
