package com.mendeley.api.network.interfaces;

import java.util.List;

import com.mendeley.api.exceptions.MendeleyException;
import com.mendeley.api.model.File;

public interface MendeleyFileInterface extends MendeleyInterface {

	public void onFilesReceived(List<File> files, MendeleyException exception);
	
	public void onFileReceived(byte[] fileData, MendeleyException exception);
	
	public void onFilePosted(File file, MendeleyException exception);
	
	public void onFileDeleted(String fileId, MendeleyException exception);

}
