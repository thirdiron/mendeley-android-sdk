package com.mendeley.api.network.interfaces;

import com.mendeley.api.exceptions.MendeleyException;
import com.mendeley.api.model.File;
import com.mendeley.api.params.Page;

import java.util.Date;
import java.util.List;

/**
 * Interface that should be implemented by the application for receiving callbacks from file network calls.
 *
 */
public interface MendeleyFileInterface  {

	public void onFilesReceived(List<File> files, Page next, Date serverTime);
	public void onFilesNotReceived(MendeleyException mendeleyException);
	
	public void onFileReceived(String fileName, String fileId);
	public void onFileNotReceived(MendeleyException mendeleyException);
	
	public void onFilePosted(File file);
	public void onFileNotPosted(MendeleyException mendeleyException);
	
	public void onFileDownloadProgress(String fileId, String documentId, int progress);

	public void onFileDeleted(String fileId);
	public void onFileNotDeleted(MendeleyException mendeleyException);


}
