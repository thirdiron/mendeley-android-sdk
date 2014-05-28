package com.mendeley.api.network;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONException;

import android.os.AsyncTask;
import android.util.Log;

import com.mendeley.api.exceptions.HttpResponseException;
import com.mendeley.api.exceptions.JsonParsingException;
import com.mendeley.api.exceptions.MendeleyException;
import com.mendeley.api.model.File;
import com.mendeley.api.network.components.FileRequestParameters;
import com.mendeley.api.network.components.MendeleyResponse;
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
	 */
	protected String getGetFilesUrl(FileRequestParameters params) {
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
				url.append(firstParam?"?":"&").append("added_since="+params.addedSince);
				firstParam = false;
			}
			if (params.deletedSince != null) {
				url.append(firstParam?"?":"&").append("deleted_since="+params.deletedSince);
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
		new GetFilesTask().execute(getGetFilesUrl(params));		  
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
	 */
	protected void doGetFile(String fileId) {
		
		Log.e(", ", getGetFileUrl(fileId));
		new GetFileTask().execute(getGetFileUrl(fileId));		  
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
	protected void doDeleteFile(String fileId) {
		new DeleteFileTask().execute(getDeleteFileUrl(fileId), fileId);		  
	}
	
	/**
	 *  Building the url string with the parameters and executes the PostFilesTask
	 * 
	 * @param fileId
	 */
	protected void doPostFile(String contentDisposition, String contentType, String link, byte[] fileData) {
		new PostFileTask().execute(contentDisposition, contentType, link, fileData);		  
	}
	
	/**
	 * Executing the api call for posting file in the background.
	 * Calling the appropriate JsonParser method to parse the json string to objects 
	 * and send the data to the relevant callback method in the MendeleyFileInterface.
	 * If the call response code is different than expected or an exception is being thrown in the process
	 * the exception will be added to the MendeleyResponse which is passed to the application via the callback.
	 */
	protected class PostFileTask extends AsyncTask<Object, Void, MendeleyException> {

		File file;
		MendeleyResponse response = new MendeleyResponse();
		int expectedResponse = 201;

		@Override
		protected MendeleyException doInBackground(Object... params) {

			String contentDisposition = (String) params[0];
			String contentType = (String) params[1];
			String link = (String) params[2];
			byte[] fileData = (byte[]) params[3];

			HttpsURLConnection con = null;

			InputStream is = null;
			OutputStream os = null;
			InputStream dataIs = null;
			
			try {
				con = getConnection(filesUrl, "POST");
				con.addRequestProperty("Content-Disposition", contentDisposition);
				con.addRequestProperty("Content-type", contentType);
				con.addRequestProperty("Link", link);
				con.connect();

				response.responseCode = con.getResponseCode();
				getResponseHeaders(con.getHeaderFields(), response);			

				Log.e("", "post file response code: " + response.responseCode);
				
				if (response.responseCode != expectedResponse) {
					return new HttpResponseException("Response code: " + response.responseCode);
				} else {			
				
					os = con.getOutputStream();
					dataIs = new ByteArrayInputStream(fileData);

					byte[] buffer = new byte[4096];
				    int length;
				    while ((length = dataIs.read(buffer)) > 0) {
				    	os.write(buffer, 0, length);
				    } 
				    os.flush();
				    os.close();
				    dataIs.close();
				    
					is = con.getInputStream();
					String jsonString = getJsonString(is);					
					is.close();
			
					JasonParser parser = new JasonParser();
					file = parser.parseFile(jsonString);
					
					return null;
				}
				 
			}	catch (IOException | JSONException e) {
				return new JsonParsingException(e.getMessage());
			} finally {
				if (os != null) {
					try {
						os.close();
					} catch (IOException e) {
						return new JsonParsingException(e.getMessage());
					}
				}
				if (dataIs != null) {
					try {
						dataIs.close();
					} catch (IOException e) {
						return new JsonParsingException(e.getMessage());
					}
				}
				if (is != null) {
					try {
						is.close();
						is = null;
					} catch (IOException e) {
						return new JsonParsingException(e.getMessage());
					}
				}
				if (con != null) {
					con.disconnect();
				}	
			}
		}
		
		@Override
		protected void onPostExecute(MendeleyException result) {
			response.mendeleyException = result;
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
	protected class GetFilesTask extends AsyncTask<String, Void, MendeleyException> {

		List<File> files;
		MendeleyResponse response = new MendeleyResponse();
		int expectedResponse = 200;

		@Override
		protected MendeleyException doInBackground(String... params) {

			String url = params[0];

			HttpsURLConnection con = null;

			InputStream is = null;
			try {
				con = getConnection(url, "GET");
				con.addRequestProperty("Content-type", "application/vnd.mendeley-file.1+json");
				con.connect();

				response.responseCode = con.getResponseCode();
				getResponseHeaders(con.getHeaderFields(), response);				

				if (response.responseCode != expectedResponse) {
					return new HttpResponseException("Response code: " + response.responseCode);
				} else {			
				
					is = con.getInputStream();
					String jsonString = getJsonString(is);					
					is.close();
			
					JasonParser parser = new JasonParser();
					files = parser.parseFileList(jsonString);
					
					return null;
				}
				 
			}	catch (IOException | JSONException e) {
				return new JsonParsingException(e.getMessage());
			} finally {
				if (is != null) {
					try {
						is.close();
						is = null;
					} catch (IOException e) {
						return new JsonParsingException(e.getMessage());
					}
				}
				if (con != null) {
					con.disconnect();
				}			if (con != null) {
					con.disconnect();
				}	
			}
		}
		
		@Override
		protected void onPostExecute(MendeleyException result) {		
			response.mendeleyException = result;
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
	protected class GetFileTask extends AsyncTask<String, Void, MendeleyException> {

		List<File> files;
		MendeleyResponse response = new MendeleyResponse();
		int expectedResponse = 303;
		byte[] fileData;
		InputStream is = null;

		@Override
		protected MendeleyException doInBackground(String... params) {

			String url = params[0];

			HttpsURLConnection con = null;

			try {
				con = getFileConnection(url, "GET");
				con.connect();
				
				response.responseCode = con.getResponseCode();
				getResponseHeaders(con.getHeaderFields(), response);			

				Log.e("", "url: " + url);
				
				Log.e("", "response.responseCode: " + response.responseCode);
				
				
//				if (response.responseCode != expectedResponse) {
//					return new HttpResponseException("Response code: " + response.responseCode);
//				} else 
//				
				{			
				
					is = con.getInputStream();

					fileData = new byte[Integer.parseInt(response.contentLength)];
					DataInputStream dis = new DataInputStream(is);
					dis.readFully(fileData);
					dis.close();
					
					return null;
				}
				 
			}	catch (IOException e) {
				
				Log.e("", "", e);
				
				return new JsonParsingException(e.getMessage());
			} finally {
				if (is != null) {
					try {
						is.close();
						is = null;
					} catch (IOException e) {
						return new JsonParsingException(e.getMessage());
					}
				}
				if (con != null) {
					con.disconnect();
				}	
			}
		}
		
		@Override
		protected void onPostExecute(MendeleyException result) {		
			response.mendeleyException = result;
			appInterface.onFileReceived(fileData, response);
		}
	}
	
	/**
	 * Executing the api call for deleting a file in the background.
	 * and send the data to the relevant callback method in the MendeleyFileInterface.
	 * If the call response code is different than expected or an exception is being thrown in the process
	 * the exception will be added to the MendeleyResponse which is passed to the application via the callback.
	 */
	protected class DeleteFileTask extends AsyncTask<String, Void, MendeleyException> {

		List<File> files;
		MendeleyResponse response = new MendeleyResponse();
		int expectedResponse = 204;
		
		String fileId;

		@Override
		protected MendeleyException doInBackground(String... params) {

			String url = params[0];
			String id = params[1];

			HttpsURLConnection con = null;

			try {
				con = getConnection(url, "DELETE");
				con.connect();
				
				response.responseCode = con.getResponseCode();
				getResponseHeaders(con.getHeaderFields(), response);			

				if (response.responseCode != expectedResponse) {
					return new HttpResponseException("Response code: " + response.responseCode);
				} else {			
				
					fileId = id;

					return null;
				}
				 
			}	catch (IOException e) {
				return new JsonParsingException(e.getMessage());
			} finally {
				if (con != null) {
					con.disconnect();
				}	
			}
		}
		
		@Override
		protected void onPostExecute(MendeleyException result) {	
			response.mendeleyException = result;
			appInterface.onFileDeleted(fileId, response);
		}
	}

	// Testing
	
	public FileNetworkProvider() {}
	
}
