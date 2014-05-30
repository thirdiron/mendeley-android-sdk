package com.mendeley.api.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONException;

import android.os.AsyncTask;
import android.util.Log;

import com.mendeley.api.exceptions.HttpResponseException;
import com.mendeley.api.exceptions.JsonParsingException;
import com.mendeley.api.exceptions.MendeleyException;
import com.mendeley.api.model.File;
import com.mendeley.api.network.DocumentNetworkProvider.GetDocumentsTask;
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
		new GetFileTask().execute(getGetFileUrl(fileId), folderPath);		  
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
	 *  Building the url string with the parameters and executes the PostFilesTask
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
	protected class PostFileTask extends AsyncTask<String, Void, MendeleyException> {

		File file;
		MendeleyResponse response = new MendeleyResponse();
		int expectedResponse = 201;

		@Override
		protected MendeleyException doInBackground(String... params) {

			String contentType = params[0];
			String documentId = params[1];
			String filePath = params[2];
			String fileName = filePath.substring(filePath.lastIndexOf(java.io.File.separator)+1);

			String contentDisposition = "attachment; filename*=UTF-8\'\'"+fileName;
			String link = "<https://mix.mendeley.com/documents/"+documentId+">; rel=\"document\"";

			HttpsURLConnection con = null;
			InputStream is = null;
			OutputStream os = null;
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
					is = con.getErrorStream();
					String responseString = "";
					if (is != null) {
						responseString = getJsonString(is);
					}
					return new HttpResponseException("Response code: " + response.responseCode + " " +responseString);
				} else {			

					is = con.getInputStream();
					String jsonString = getJsonString(is);					
					is.close();
			
					JasonParser parser = new JasonParser();
					file = parser.parseFile(jsonString);
					
					return null;
				}
				 
			}	catch (IOException | JSONException e) {
				return new JsonParsingException(e.getMessage());
			} catch (NullPointerException e) {
				return new MendeleyException(e.getMessage());
			} 
			finally {
				if (os != null) {
					try {
						os.close();
						os = null;
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
				if (fileInputStream != null) {
					try {
						fileInputStream.close();
						fileInputStream = null;
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
		MendeleyResponse downloadResponse = new MendeleyResponse();
		int expectedResponse = 303;
		byte[] fileData;
		InputStream is = null;
		String fileName;

		@Override
		protected MendeleyException doInBackground(String... params) {

			String url = params[0];
			String folderPath = params[1];
			
			HttpsURLConnection con = null;
			InputStream is = null;
			FileOutputStream fileOutputStream = null;

			try {
				con = getConnection(url, "GET");
				con.setInstanceFollowRedirects(false);
				con.connect();
				
				response.responseCode = con.getResponseCode();
				getResponseHeaders(con.getHeaderFields(), response);			

				if (response.responseCode != expectedResponse) {
					is = con.getErrorStream();
					String responseString = "";
					if (is != null) {
						responseString = getJsonString(is);
					}
					return new HttpResponseException("Response code: " + response.responseCode + " " + responseString);
				} else {		
					con.disconnect();
					
					con = getDownloadConnection(response.location, "GET");
					con.connect();
					
					int responseCode = con.getResponseCode();
					
					if (responseCode != 200) {
						is = con.getErrorStream();
						String responseString = "";
						if (is != null) {
							responseString = getJsonString(is);
						}
						return new HttpResponseException("Response code: " + response.responseCode + " " + responseString);
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
						is.close();
						
						return null;
					}
				}
				 
			}	catch (IOException e) {
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
				if (fileOutputStream != null) {
					try {
						fileOutputStream.close();
						fileOutputStream = null;
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
			appInterface.onFileReceived(fileName, response);
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
