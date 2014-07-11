package com.mendeley.api.network;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import android.os.AsyncTask;
import android.util.Log;

import com.mendeley.api.exceptions.FileDownloadException;
import com.mendeley.api.exceptions.HttpResponseException;
import com.mendeley.api.exceptions.JsonParsingException;
import com.mendeley.api.exceptions.MendeleyException;
import com.mendeley.api.exceptions.NoMorePagesException;
import com.mendeley.api.model.File;
import com.mendeley.api.params.FileRequestParameters;
import com.mendeley.api.params.Page;
import com.mendeley.api.network.interfaces.MendeleyFileInterface;

/**
 * NetworkProvider class for Files API calls
 *
 */
public class FileNetworkProvider extends NetworkProvider {
	
	private Map<String, NetworkTask> fileTaskMap = new HashMap<String, NetworkTask>();
	private GetFilesTask getFilesTask;
	
	private static String filesUrl = apiUrl + "files";
	MendeleyFileInterface appInterface;
	
	/**
	 * Constructor that takes MendeleyFileInterface instance which will be used to send callbacks to the application
	 * 
	 * @param appInterface the instance of MendeleyFileInterface
	 */
    public FileNetworkProvider(MendeleyFileInterface appInterface) {
		this.appInterface = appInterface;
	}
	
	/**
	 * Building the url for get files
	 * 
	 * @param params the file request parameters
	 * @return the url string
	 * @throws UnsupportedEncodingException 
	 */
	protected String getGetFilesUrl(FileRequestParameters params) throws UnsupportedEncodingException {
		boolean firstParam = true;
		StringBuilder url = new StringBuilder();
		url.append(filesUrl);
		
		if (params != null) {
			if (params.documentId != null) {
				url.append(firstParam?"?":"&").append("document_id="+params.documentId);
				firstParam = false;
			}
			if (params.groupId != null) {
				url.append(firstParam?"?":"&").append("group_id="+params.groupId);
				firstParam = false;
			}
			if (params.addedSince != null) {
				url.append(firstParam?"?":"&").append("added_since="+URLEncoder.encode(params.addedSince, "ISO-8859-1"));
				firstParam = false;
			}
			if (params.deletedSince != null) {
				url.append(firstParam?"?":"&").append("deleted_since="+URLEncoder.encode(params.deletedSince, "ISO-8859-1"));
			}
			if (params.limit != null) {
				url.append(firstParam?"?":"&").append("limit="+params.limit);
				firstParam = false;
			}
			if (params.marker != null) {
				url.append(firstParam?"?":"&").append("marker="+params.marker);
				firstParam = false;
			}
			if (params.reverse != null) {
				url.append(firstParam?"?":"&").append("reverse="+params.reverse);
				firstParam = false;
			}
		}
		
		return url.toString();
	}

	/**
	 * Getting the appropriate url string and executes the GetFilesTask
	 * 
	 * @param params the file request parameters
	 */
    public void doGetFiles(FileRequestParameters params) {
		try {
			getFilesTask = new GetFilesTask();		

			String[] paramsArray = new String[]{getGetFilesUrl(params)};
			getFilesTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, paramsArray);
		}
		catch (UnsupportedEncodingException e) {
            appInterface.onFilesNotReceived(new MendeleyException(e.getMessage()));
		}
	}

    /**
     * Getting the appropriate url string and executes the GetFilesTask
     *
     * @param next reference to next page
     */
    public void doGetFiles(Page next) {
        if (Page.isValidPage(next)) {
        	
        	String[] paramsArray = new String[]{next.link};			
            new GetFilesTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, paramsArray);
        } else {
            appInterface.onFilesNotReceived(new NoMorePagesException());
        }
    }

    /**
	 * Building the url for get files
	 * 
	 * @param fileId the id of the file to get
	 * @return the url string
	 */
	protected String getGetFileUrl(String fileId) {
		return filesUrl+"/"+fileId;
	}
	
	/**
	 *  Getting the appropriate url string and executes the GetFileTask
	 * 
	 * @param fileId the id of the file to get
	 * @param folderPath the path in which to save the file
	 */
    public void doGetFile(final String fileId, final String documentId, final String folderPath) {
		final GetFileTask fileTask = new GetFileTask();
		fileTaskMap.put(fileId, fileTask);
		String[] params = new String[]{getGetFileUrl(fileId), folderPath, fileId, documentId};
		fileTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
	}
	
	/**
	 * Cancelling the NetworkTask that is currently download the file with the given fileId.
	 * @param fileId the id of the file 
	 */
    public void cancelDownload(String fileId) {
		GetFileTask task = (GetFileTask) fileTaskMap.get(fileId);
		if (task != null) {
			task.cancel(true);
		}
	}
	
	/**
	 * Cancelling GetFilesTask if it is currently running
	 */
    public void cancelGetFiles() {
		if (getFilesTask != null) {
			getFilesTask.cancel(true);
		}
	}
	
	/**
	 * Building the url for delete files
	 * 
	 * @param fileId the id of the file to delete
	 * @return the url string
	 */
	protected String getDeleteFileUrl(String fileId) {
		return filesUrl+"/"+fileId;
	}
	
	/**
	 * Getting the appropriate url string and executes the DeleteFileTask
	 * 
	 * @param fileId the id of the file to delete
	 */
    public void doDeleteFile(String fileId) { Log.e("", "fileId to delete: " + fileId);
	
		String[] paramsArray = new String[]{getDeleteFileUrl(fileId), fileId};			
		new DeleteFileTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, paramsArray);
	}
	
	/**
	 *  Building the url string with the parameters and executes the PostFileTask
	 * 
	 * @param contentType content type of the file
	 * @param documentId the id of the document the file is related to
	 * @param filePath the absolute file path
	 */
    public void doPostFile(String contentType, String documentId, String filePath) {
		
		String[] paramsArray = new String[]{contentType, documentId, filePath};			
		new PostFileTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, paramsArray); 
	}
	
	/**
	 * Executing the api call for posting file in the background.
	 * Calling the appropriate JsonParser method to parse the json string to objects 
	 * and send the data to the relevant callback method in the MendeleyFileInterface.
	 * If the call response code is different than expected or an exception is being thrown in the process
	 * the exception will be added to the MendeleyResponse which is passed to the application via the callback.
	 */
	protected class PostFileTask extends NetworkTask {

		File file;

		@Override
		protected int getExpectedResponse() {
			return 201;
		}
		
		@Override
		protected MendeleyException doInBackground(String... params) {

			String contentType = params[0];
			String documentId = params[1];
			String filePath = params[2];
			String fileName = filePath.substring(filePath.lastIndexOf(java.io.File.separator)+1);

			String contentDisposition = "attachment; filename*=UTF-8\'\'"+fileName;
			String link = "<https://api.mendeley.com/documents/"+documentId+">; rel=\"document\"";

			FileInputStream fileInputStream = null;
			
			try {
				java.io.File sourceFile = new java.io.File(filePath);
				fileInputStream = new FileInputStream(sourceFile);
				int bytesAvailable;
				int maxBufferSize = 4096;
				int bufferSize;
				byte[] buffer;
				int bytesRead;
				
				con = getConnection(filesUrl, "POST");
				con.addRequestProperty("Content-Disposition", contentDisposition);
				con.addRequestProperty("Content-type", contentType);
				con.addRequestProperty("Link", link);

				os = new DataOutputStream(con.getOutputStream());

			    bytesAvailable = fileInputStream.available();
			    bufferSize = Math.min(bytesAvailable, maxBufferSize);
			    buffer = new byte[bufferSize];
			 
			    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			 
			    while (bytesRead > 0)
			    {
			    	os.write(buffer, 0, bufferSize);
			        bytesAvailable = fileInputStream.available();
			        bufferSize = Math.min(bytesAvailable, maxBufferSize);
			        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
			    }

			    os.close();
			    fileInputStream.close();
				con.connect();

				getResponseHeaders();

				if (con.getResponseCode() != getExpectedResponse()) {
					return new HttpResponseException(getErrorMessage(con));
				} else {			

					is = con.getInputStream();
					String jsonString = getJsonString(is);					
					is.close();
			
					JsonParser parser = new JsonParser();
					file = parser.parseFile(jsonString);
					
					return null;
				}
				 
			}	catch (IOException | JSONException e) {
				return new JsonParsingException(e.getMessage());
			} catch (NullPointerException e) {
				return new MendeleyException(e.getMessage());
			} 
			finally {
				closeConnection();
				if (fileInputStream != null) {
					try {
						fileInputStream.close();
						fileInputStream = null;
					} catch (IOException e) {
						return new JsonParsingException(e.getMessage());
					}
				}
			}
		}
		
		@Override
		protected void onSuccess() {
			appInterface.onFilePosted(file);
		}

		@Override
		protected void onFailure(MendeleyException exception) {
			appInterface.onFileNotPosted(exception);				
		}
	}
	
	/**
	 * Executing the api call for getting files in the background.
	 * Calling the appropriate JsonParser method to parse the json string to objects 
	 * and send the data to the relevant callback method in the MendeleyFileInterface.
	 * If the call response code is different than expected or an exception is being thrown in the process
	 * the exception will be added to the MendeleyResponse which is passed to the application via the callback.
	 */
	protected class GetFilesTask extends NetworkTask {

		List<File> files;

		@Override
		protected int getExpectedResponse() {
			return 200;
		}
		
		@Override
		protected MendeleyException doInBackground(String... params) {

			String url = params[0];

			try {
				con = getConnection(url, "GET");
				con.addRequestProperty("Content-type", "application/vnd.mendeley-file.1+json");
				con.connect();

				getResponseHeaders();

				if (con.getResponseCode() != getExpectedResponse()) {
					return new HttpResponseException(getErrorMessage(con));
				} else if (!isCancelled()) {			
				
					is = con.getInputStream();
					String jsonString = getJsonString(is);					
			
					JsonParser parser = new JsonParser();
					files = parser.parseFileList(jsonString);
					
					return null;
				} else {
					return new MendeleyException("Operation cancelled by the user");
				}
				 
			}	catch (IOException | JSONException e) {
				return new JsonParsingException(e.getMessage());
			} finally {
				closeConnection();
			}
		}
		
	    @Override
	    protected void onCancelled (MendeleyException result) {
	    	appInterface.onFilesNotReceived(new MendeleyException("Operation cancelled by the user"));	
	    	getFilesTask = null;
	    }
		
		@Override
		protected void onSuccess() {		
			appInterface.onFilesReceived(files, next);
			getFilesTask = null;
		}

		@Override
		protected void onFailure(MendeleyException exception) {		
			appInterface.onFilesNotReceived(exception);		
			getFilesTask = null;
		}
	}
	
	/**
	 * Executing the api call for getting a file in the background.
	 * Calling the appropriate JsonParser method to parse the json string to object
	 * and send the data to the relevant callback method in the MendeleyFileInterface.
	 * If the call response code is different than expected or an exception is being thrown in the process
	 * the exception will be added to the MendeleyResponse which is passed to the application via the callback.
	 */
	protected class GetFileTask extends NetworkTask {

		List<File> files;
		byte[] fileData;
		String fileName;
		String fileId;
		String documentId;
		String filePath;

		@Override
		protected int getExpectedResponse() {
			return 303;
		}
		
		@Override
		protected MendeleyException doInBackground(String... params) {
			
			String url = params[0];
			String folderPath = params[1];
			fileId = params[2];
			documentId = params[3];

			FileOutputStream fileOutputStream = null;

			try {
				con = getConnection(url, "GET");
				con.setInstanceFollowRedirects(false);
				con.connect();
				
				getResponseHeaders();

				if (con.getResponseCode() != getExpectedResponse()) {
					return new FileDownloadException(getErrorMessage(con), fileId);
				} else {		
					con.disconnect();
					
					con = getDownloadConnection(location, "GET");
					con.connect();
					
					int responseCode = con.getResponseCode();
					
					if (responseCode != 200) {
						return new FileDownloadException(getErrorMessage(con), fileId);
					} else {
						String content = con.getHeaderFields().get("Content-Disposition").get(0);
						fileName = content.substring(content.indexOf("\"")+1, content.lastIndexOf("\""));
						
						int fileLength = con.getContentLength();
						is = con.getInputStream();			
						filePath = folderPath+java.io.File.separator+fileName;
						fileOutputStream = new FileOutputStream(new java.io.File(filePath));
						
						byte data[] = new byte[1024];
			            long total = 0;
			            int count;
			            while ((count = is.read(data)) != -1 && !isCancelled()) {
			                total += count;
			                if (fileLength > 0) 
			                    publishProgress((int) (total * 100 / fileLength));
			                fileOutputStream.write(data, 0, count);
			            }
					    fileOutputStream.close();
						
						return null;
					}
				}
				 
			}	catch (IOException e) {
				return new FileDownloadException(e.getMessage(), fileId);
			} finally {
				closeConnection();
				if (fileOutputStream != null) {
					try {
						fileOutputStream.close();
						fileOutputStream = null;
					} catch (IOException e) {
						return new FileDownloadException(e.getMessage(), fileId);
					}
				}
			}
		} 
		
	    @Override
	    protected void onProgressUpdate(Integer... progress) {
	    	appInterface.onFileDownloadProgress(fileId, documentId, progress[0]);
	    }
	    
	    @Override
	    protected void onCancelled (MendeleyException result) {
	    	fileTaskMap.remove(fileId);
	    	
	    	if (filePath != null) {
		    	java.io.File file = new java.io.File(filePath);
		    	file.delete();
	    	}
	    }
	    
		@Override
		protected void onSuccess() {		
			fileTaskMap.remove(fileId);
			appInterface.onFileReceived(fileName, fileId);
		}

		@Override
		protected void onFailure(MendeleyException exception) {		
			fileTaskMap.remove(fileId);
			appInterface.onFileNotReceived(exception);				
		}
	}
	
	/**
	 * Executing the api call for deleting a file in the background.
	 * and send the data to the relevant callback method in the MendeleyFileInterface.
	 * If the call response code is different than expected or an exception is being thrown in the process
	 * the exception will be added to the MendeleyResponse which is passed to the application via the callback.
	 */
	protected class DeleteFileTask extends NetworkTask {
		List<File> files;
		String fileId;

		@Override
		protected int getExpectedResponse() {
			return 204;
		}
		
		@Override
		protected MendeleyException doInBackground(String... params) {
			String url = params[0];
			String id = params[1];

			try {
				con = getConnection(url, "DELETE");
				con.connect();
				
				getResponseHeaders();

				if (con.getResponseCode() != getExpectedResponse()) {
					return new HttpResponseException(getErrorMessage(con));
				} else {			
				
					fileId = id;

					return null;
				}
				 
			}	catch (IOException e) {
				return new JsonParsingException(e.getMessage());
			} finally {
				closeConnection();
			}
		}
		
		@Override
		protected void onSuccess() {	
			appInterface.onFileDeleted(fileId);
		}
		
		@Override
		protected void onFailure(MendeleyException exception) {	
			appInterface.onFileNotDeleted(exception);				
		}
	}

	// Testing
	
	public FileNetworkProvider() {}
	
}
