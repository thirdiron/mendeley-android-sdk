package com.mendeley.api.network.interfaces;

import java.util.List;

import com.mendeley.api.model.File;
import com.mendeley.api.network.components.MendeleyResponse;

/**
 * Interface that should be implemented by the application for receiving callbacks from file network calls.
 *
 */
public interface MendeleyFileInterface extends MendeleyInterface {

	public void onFilesReceived(List<File> files, MendeleyResponse mendeleyResponse);
	
	public void onFileReceived(byte[] fileData, MendeleyResponse mendeleyResponse);
	
	public void onFilePosted(File file, MendeleyResponse mendeleyResponse);
	
	public void onFileDeleted(String fileId, MendeleyResponse mendeleyResponse);

}
