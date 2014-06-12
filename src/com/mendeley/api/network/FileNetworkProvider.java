package com.mendeley.api.network;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.json.JSONException;

import android.util.Log;

import com.mendeley.api.exceptions.HttpResponseException;
import com.mendeley.api.exceptions.JsonParsingException;
import com.mendeley.api.exceptions.MendeleyException;
import com.mendeley.api.model.File;
import com.mendeley.api.network.components.FileRequestParameters;
import com.mendeley.api.network.interfaces.MendeleyFileInterface;

/**
 * NetworkProvider class for Files API calls
 *
 */
public class FileNetworkProvider extends NetworkProvider {
	
	private static String filesUrl = apiUrl + "files";
	MendeleyFileInterface appInterface;
	
	/**
	 * Constructor that takes MendeleyFileInterface instance which will be used to send callbacks to the application
	 * 
	 * @param appInterface the instance of MendeleyFileInterface
	 */
	FileNetworkProvider(MendeleyFileInterface appInterface) {
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
	protected void doGetFiles(FileRequestParameters params) {		
		try {
			new GetFilesTask().execute(getGetFilesUrl(params));		
		}
		catch (UnsupportedEncodingException e) {
			appInterface.onAPICallFail(new MendeleyException(e.getMessage()));
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
	protected void doGetFile(String fileId, String folderPath) {
		new GetFileTask().execute(getGetFileUrl(fileId), folderPath, fileId);		  
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
	protected void doDeleteFile(String fileId) { Log.e("", "fileId to delete: " + fileId);
		new DeleteFileTask().execute(getDeleteFileUrl(fileId), fileId);		  
	}
	
	/**
	 *  Building the url string with the parameters and executes the PostFileTask
	 * 
	 * @param contentType content type of the file
	 * @param documentId the id of the document the file is related to
	 * @param filePath the absolute file path
	 */
	protected void doPostFile(String contentType, String documentId, String filePath) {
		new PostFileTask().execute(contentType, documentId, filePath);		  
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
		protected void onPreExecute() {
			expectedResponse = 201;
		}
		
		@Override
		protected MendeleyException doInBackground(String... params) {

			String contentType = params[0];
			String documentId = params[1];
			String filePath = params[2];
			String fileName = filePath.substring(filePath.lastIndexOf(java.io.File.separator)+1);

			String contentDisposition = "attachment; filename*=UTF-8\'\'"+fileName;
			String link = "<https://mix.mendeley.com/documents/"+documentId+">; rel=\"document\"";

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

				response.responseCode = con.getResponseCode();
				getResponseHeaders(con.getHeaderFields(), response);			

				if (response.responseCode != expectedResponse) {
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
		protected void onPostExecute(MendeleyException result) {
			super.onPostExecute(result);
			appInterface.onFilePosted(file, response);			
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
		protected void onPreExecute() {
			expectedResponse = 200;
		}
		
		@Override
		protected MendeleyException doInBackground(String... params) {

			String url = params[0];

			try {
				con = getConnection(url, "GET");
				con.addRequestProperty("Content-type", "application/vnd.mendeley-file.1+json");
				con.connect();

				response.responseCode = con.getResponseCode();
				getResponseHeaders(con.getHeaderFields(), response);				

				if (response.responseCode != expectedResponse) {
					return new HttpResponseException(getErrorMessage(con));
				} else {			
				
					is = con.getInputStream();
					String jsonString = getJsonString(is);					
			
					JsonParser parser = new JsonParser();
					files = parser.parseFileList(jsonString);
					
					return null;
				}
				 
			}	catch (IOException | JSONException e) {
				return new JsonParsingException(e.getMessage());
			} finally {
				closeConnection();
			}
		}
		
		@Override
		protected void onPostExecute(MendeleyException result) {		
			super.onPostExecute(result);
			appInterface.onFilesReceived(files, response);			
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

		@Override
		protected void onPreExecute() {
			expectedResponse = 303;
		}
		
		@Override
		protected MendeleyException doInBackground(String... params) {

			String url = params[0];
			String folderPath = params[1];
			fileId = params[2];
			
			FileOutputStream fileOutputStream = null;

			try {
				con = getConnection(url, "GET");
				con.setInstanceFollowRedirects(false);
				con.connect();
				
				response.responseCode = con.getResponseCode();
				getResponseHeaders(con.getHeaderFields(), response);			

				if (response.responseCode != expectedResponse) {
					return new HttpResponseException(getErrorMessage(con));
				} else {		
					con.disconnect();
					
					con = getDownloadConnection(response.location, "GET");
					con.connect();
					
					int responseCode = con.getResponseCode();
					
					if (responseCode != 200) {
						return new HttpResponseException(getErrorMessage(con));
					} else {
						String content = con.getHeaderFields().get("Content-Disposition").get(0);
						fileName = content.substring(content.indexOf("\"")+1, content.lastIndexOf("\""));
						
						is = con.getInputStream();			
						fileOutputStream = new FileOutputStream(new java.io.File(folderPath+java.io.File.separator+fileName));
						byte[]  buffer = new byte[1024];
				        int bufferLength = 0; 
	
				        while ((bufferLength = is.read(buffer)) > 0) {
				        	fileOutputStream.write(buffer, 0, bufferLength);
				        }
	
					    fileOutputStream.close();
						
						return null;
					}
				}
				 
			}	catch (IOException e) {
				return new JsonParsingException(e.getMessage());
			} finally {
				closeConnection();
				if (fileOutputStream != null) {
					try {
						fileOutputStream.close();
						fileOutputStream = null;
					} catch (IOException e) {
						return new JsonParsingException(e.getMessage());
					}
				}
			}
		}
		
		@Override
		protected void onPostExecute(MendeleyException result) {		
			super.onPostExecute(result);
			appInterface.onFileReceived(fileName, fileId, response);
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
		protected void onPreExecute() {
			expectedResponse = 204;
		}
		
		@Override
		protected MendeleyException doInBackground(String... params) {
			String url = params[0];
			String id = params[1];

			try {
				con = getConnection(url, "DELETE");
				con.connect();
				
				response.responseCode = con.getResponseCode();
				getResponseHeaders(con.getHeaderFields(), response);			

				if (response.responseCode != expectedResponse) {
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
		protected void onPostExecute(MendeleyException result) {	
			super.onPostExecute(result);
			appInterface.onFileDeleted(fileId, response);
		}
	}

	// Testing
	
	public FileNetworkProvider() {}
	
}
