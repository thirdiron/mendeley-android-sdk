package com.mendeley.api.network;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;

import android.os.AsyncTask;

import com.mendeley.api.exceptions.HttpResponseException;
import com.mendeley.api.exceptions.JsonParsingException;
import com.mendeley.api.exceptions.MendeleyException;
import com.mendeley.api.model.Folder;
import com.mendeley.api.network.NetworkProvider.NetworkTask;
import com.mendeley.api.network.components.FolderRequestParameters;
import com.mendeley.api.network.components.MendeleyResponse;
import com.mendeley.api.network.interfaces.MendeleyFolderInterface;

/**
 * NetworkProvider class for Folder API calls
 *
 */
public class FolderNetworkProvider extends NetworkProvider{
	
	private static String foldersUrl = apiUrl + "folders";
	MendeleyFolderInterface appInterface;
	
	/**
	 * Constructor that takes MendeleyFolderInterface instance which will be used to send callbacks to the application
	 * 
	 * @param appInterface the instance of MendeleyFolderInterface
	 */
	FolderNetworkProvider(MendeleyFolderInterface appInterface) {
		this.appInterface = appInterface;
	}
	
	/**
	 * Building the url for get folders
	 * 
	 * @param params folder request parameters object
	 * @return the url string
	 */
	protected String getGetFoldersUrl(FolderRequestParameters params) {
		String url = foldersUrl;
		if (params != null) {
			if (params.groupId != null) {
				url += "?group_id="+params.groupId;
			}
		}
		
		return url.toString();
	}

	/**
	 * Getting the appropriate url string and executes the GetFoldersTask
	 * 
	 * @param params folder request parameters object
	 */
	protected void doGetFolders(FolderRequestParameters params) {
		new GetFoldersTask().execute(getGetFoldersUrl(params));		  
	}

	/**
	 * Building the url for get folder
	 * 
	 * @param folderId the folder id to get
	 * @return the url string
	 */
	protected String getGetFolderUrl(String folderId) {
		return foldersUrl+"/"+folderId;
	}
	
	/**
	 * Getting the appropriate url string and executes the GetFolderTask
	 * 
	 * @param folderId the folder id to get
	 */
	protected void doGetFolder(String folderId) {
		new GetFolderTask().execute(getGetFolderUrl(folderId));		  
	}
	
	/**
	 * Building the url for get folder document ids
	 * 
	 * @param folderId the folder id
	 * @return the url string
	 */
	protected String getGetFolderDocumentIdsUrl(String folderId) {
		return foldersUrl + "/"+folderId + "/documents";
	}

	/**
	 * Getting the appropriate url string and executes the GetFolderDocumentIdsTask
	 * 
	 * @param folderId the folder id
	 */
	protected void doGetFolderDocumentIds(String folderId) {
		new GetFolderDocumentIdsTask().execute(getGetFolderDocumentIdsUrl(folderId));		  
	}
	
	/**
	 * Building the url string with the parameters and executes the PostFolderTask.
	 * 
	 * @param folder the folder to post
	 */
	protected void doPostFolder(Folder folder) {

		JasonParser parser = new JasonParser();
		try {
			new PostFolderTask().execute(foldersUrl, parser.jsonFromFolder(folder));			
		} catch (JSONException e) {
			appInterface.onAPICallFail(new JsonParsingException(e.getMessage()));
		}
	}
	
	/**
	 * Building the url for post document to folder
	 * 
	 * @param folderId the folder id
	 * @return the url string
	 */
	protected String getPostDocumentToFolderUrl(String folderId) {
		return foldersUrl + "/"+folderId + "/documents";
	}
	
	/**
	 * Getting the appropriate url string and executes the PostDocumentToFolderTask.
	 * 
	 * @param folderId the folder id
	 * @param documentId the id of the document to add to the folder
	 */
	protected void doPostDocumentToFolder(String folderId, String documentId) {
		String documentString = null;
		if (documentId != null && !documentId.isEmpty()) {
			JasonParser parser = new JasonParser();
			try {
				documentString = parser.jsonFromDocumentId(documentId);
			} catch (JSONException e) {
				appInterface.onAPICallFail(new JsonParsingException(e.getMessage()));
			}
		}	
		new PostDocumentToFolderTask().execute(getPostDocumentToFolderUrl(folderId), documentString, folderId);	
	}
	
	/**
	 * Building the url for delete folder
	 * 
	 * @param folderId the folder id
	 * @return the url string
	 */
	protected String getDeleteFolderUrl(String folderId) {
		return foldersUrl + "/"+folderId;
	}
	
	/**
	 * Getting the appropriate url string and executes the DeleteFolderTask.
	 * 
	 * @param folderId the id of the folder to delete
	 */
	protected void doDeleteFolder(String folderId) {
		new DeleteFolderTask().execute(getDeleteFolderUrl(folderId), folderId);	
	}
	
	/**
	 * Building the url for delete document from folder
	 * 
	 * @param folderId the id of the folder
	 * @param documentId the id of the document to delete
	 */
	protected String getDeleteDocumentFromFolderUrl(String folderId, String documentId) {
		return foldersUrl+"/"+folderId+"/documents"+documentId;
	}
	
	/**
	 * Getting the appropriate url string and executes the DeleteDocumentFromFolderTask.
	 * 
	 * @param folderId the id of the folder
	 * @param documentId the id of the document to delete
	 */
	protected void doDeleteDocumentFromFolder(String folderId, String documentId) {
		new DeleteDocumentFromFolderTask().execute(getDeleteDocumentFromFolderUrl(folderId, documentId), documentId);	
	}
	
	/**
	 * Building the url for patch folder
	 * 
	 * @param folderId the folder id to patch
	 * @return the url string
	 */
	protected String getPatchFolderUrl(String folderId) {
		return foldersUrl + "/"+folderId;
	}
	
	/**
	 * Getting the appropriate url string and executes the PatchFolderTask.
	 * 
	 * @param folderId the folder id to patch
	 * @param folder the Folder object
	 */
	protected void doPatchFolder(String folderId, Folder folder) {
		JasonParser parser = new JasonParser();
		String folderString = null;
		try {
			folderString  = parser.jsonFromFolder(folder);
		} catch (JSONException e) {
			appInterface.onAPICallFail(new JsonParsingException(e.getMessage()));
		}
		
		new PatchFolderTask().execute(getPatchFolderUrl(folderId), folderId, folderString);	
	}
	
	/**
	 * Executing the api call for patching a folder in the background.
	 * Calling the appropriate JsonParser method to parse the json string to objects 
	 * and send the data to the relevant callback method in the MendeleyFolderInterface.
	 * If the call response code is different than expected or an exception is being thrown in the process
	 * the exception will be added to the MendeleyResponse which is passed to the application via the callback.
	 */
	protected class PatchFolderTask extends NetworkTask {

		String folderId = null;

		@Override
		protected void onPreExecute() {
			expectedResponse = 200;
		}
		
		@Override
		protected MendeleyException doInBackground(String... params) {
			
			String url = params[0];
			String id = params[1];
			String jsonString = params[2];
			
			HttpClient httpclient = new DefaultHttpClient();
			HttpPatch httpPatch = getFolderHttpPatch(url);

	        try {
	        	
	        	httpPatch.setEntity(new StringEntity(jsonString));
	        	HttpResponse response = httpclient.execute(httpPatch);				
				int responseCode = response.getStatusLine().getStatusCode();	        	
				
				if (responseCode != expectedResponse) {
					return new HttpResponseException(getErrorMessage(response));
				} else {
					folderId = id;
					return null;
				}
			} catch (IOException e) {
				return new JsonParsingException(e.getMessage());
			} 			
		}
		
		@Override
		protected void onPostExecute(MendeleyException result) {
			super.onPostExecute(result);
			appInterface.onFolderPatched(folderId, response);
		}
	}
	
	/**
	 * Executing the api call for deleting a document from folder in the background.
	 * sending the data to the relevant callback method in the MendeleyFolderInterface.
	 * If the call response code is different than expected or an exception is being thrown in the process
	 * the exception will be added to the MendeleyResponse which is passed to the application via the callback.
	 */
	protected class DeleteDocumentFromFolderTask extends NetworkTask {

		String documentId = null;
		
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
					documentId = id;
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
			appInterface.onFolderDocumentDeleted(documentId, response);
		}
	}
	
	/**
	 * Executing the api call for deleting a folder in the background.
	 * sending the data to the relevant callback method in the MendeleyFolderInterface.
	 * If the call response code is different than expected or an exception is being thrown in the process
	 * the exception will be added to the MendeleyResponse which is passed to the application via the callback.
	 */
	protected class DeleteFolderTask extends NetworkTask {

		String folderId = null;

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
					folderId = id;
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
			appInterface.onFolderDeleted(folderId, response);
		}
	}
	
	/**
	 * Executing the api call for posting a document to a folder in the background.
	 * sending the data to the relevant callback method in the MendeleyFolderInterface.
	 * If the call response code is different than expected or an exception is being thrown in the process
	 * the exception will be added to the MendeleyResponse which is passed to the application via the callback.
	 */
	protected class PostDocumentToFolderTask extends NetworkTask {

		String folderId;
		
		@Override
		protected void onPreExecute() {
			expectedResponse = 201;
		}
		
		@Override
		protected MendeleyException doInBackground(String... params) {
			
			String url = params[0];
			String jsonString = params[1];
			
			try {
				con = getConnection(url, "POST");		
				con.addRequestProperty("Content-type", "application/vnd.mendeley-folder-add-document.1+json");
				con.connect();
	
				os = con.getOutputStream();
				BufferedWriter writer = new BufferedWriter(
				        new OutputStreamWriter(os, "UTF-8")); 
				writer.write(jsonString);
				writer.flush();
				writer.close();
				os.close();
				
				response.responseCode = con.getResponseCode();
				getResponseHeaders(con.getHeaderFields(), response);	

				if (response.responseCode != expectedResponse) {
					return new HttpResponseException(getErrorMessage(con));
				} else {

					folderId = params[2];
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
			appInterface.onDocumentPostedToFolder(folderId, response);
		}
	}
	
	/**
	 * Executing the api call for posting a folder in the background.
	 * sending the data to the relevant callback method in the MendeleyFolderInterface.
	 * If the call response code is different than expected or an exception is being thrown in the process
	 * the exception will be added to the MendeleyResponse which is passed to the application via the callback.
	 */
	protected class PostFolderTask extends NetworkTask {

		Folder folder;

		@Override
		protected void onPreExecute() {
			expectedResponse = 201;
		}
		
		@Override
		protected MendeleyException doInBackground(String... params) {
			
			String url = params[0];
			String jsonString = params[1];

			try {
				con = getConnection(url, "POST");		
				con.addRequestProperty("Content-type", "application/vnd.mendeley-folder.1+json");
				con.connect();
	
				os = con.getOutputStream();
				BufferedWriter writer = new BufferedWriter(
				        new OutputStreamWriter(os, "UTF-8")); 
				writer.write(jsonString);
				writer.flush();
				writer.close();
				os.close();
				
				response.responseCode = con.getResponseCode();
				getResponseHeaders(con.getHeaderFields(), response);	

				if (response.responseCode != expectedResponse) {
					return new HttpResponseException(getErrorMessage(con));
				} else {

					is = con.getInputStream();
					String responseString = getJsonString(is);					

					JasonParser parser = new JasonParser();
					folder = parser.parseFolder(responseString);
					
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
			appInterface.onFolderPosted(folder, response);
		}
	}
	
	/**
	 * Executing the api call for getting folders in the background.
	 * Calling the appropriate JsonParser method to parse the json string to object
	 * and send the data to the relevant callback method in the MendeleyFolderInterface.
	 * If the call response code is different than expected or an exception is being thrown in the process
	 * the exception will be added to the MendeleyResponse which is passed to the application via the callback.
	 */
	protected class GetFolderDocumentIdsTask extends NetworkTask {

		List<String> documentIds;

		@Override
		protected void onPreExecute() {
			expectedResponse = 200;
		}
		
		@Override
		protected MendeleyException doInBackground(String... params) {

			String url = params[0];

			try {
				con = getConnection(url, "GET");
				con.addRequestProperty("Content-type", "application/vnd.mendeley-folder-documentids.1+json");
				con.connect();
				
				response.responseCode = con.getResponseCode();
				getResponseHeaders(con.getHeaderFields(), response);				

				if (response.responseCode != expectedResponse) {
					return new HttpResponseException(getErrorMessage(con));
				} else {			
				
					is = con.getInputStream();
					String jsonString = getJsonString(is);					

					JasonParser parser = new JasonParser();
					documentIds = parser.parseDocumentIds(jsonString);

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
			appInterface.onFolderDocumentIdsReceived(documentIds, response);
		}
	}
	
	/**
	 * Executing the api call for getting a folder in the background.
	 * Calling the appropriate JsonParser method to parse the json string to object
	 * and send the data to the relevant callback method in the MendeleyFolderInterface.
	 * If the call response code is different than expected or an exception is being thrown in the process
	 * the exception will be added to the MendeleyResponse which is passed to the application via the callback.
	 */
	protected class GetFolderTask extends NetworkTask {

		Folder folder;

		@Override
		protected void onPreExecute() {
			expectedResponse = 200;
		}
		
		@Override
		protected MendeleyException doInBackground(String... params) {

			String url = params[0];

			try {
				con = getConnection(url, "GET");
				con.addRequestProperty("Content-type", "application/vnd.mendeley-folder.1+json");
				con.connect();
				
				response.responseCode = con.getResponseCode();
				getResponseHeaders(con.getHeaderFields(), response);					

				if (response.responseCode != expectedResponse) {
					return new HttpResponseException(getErrorMessage(con));
				} else {			
				
					is = con.getInputStream();
					String jsonString = getJsonString(is);					

					JasonParser parser = new JasonParser();
					folder = parser.parseFolder(jsonString);

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
			appInterface.onFolderReceived(folder, response);
		}
	}
	
	/**
	 * Executing the api call for getting folders in the background.
	 * Calling the appropriate JsonParser method to parse the json string to object
	 * and send the dresponsehe call response code is different than expected or an exception is being thrown in the process
	 * creates a new MendeleyException with the relevant information which will be passed to the application via the callback.
	 */
	protected class GetFoldersTask extends NetworkTask {

		List<Folder> folders;

		@Override
		protected void onPreExecute() {
			expectedResponse = 200;
		}
		
		@Override
		protected MendeleyException doInBackground(String... params) {

			String url = params[0];
			
			try {
				con = getConnection(url, "GET");
				con.addRequestProperty("Content-type", "application/vnd.mendeley-folder.1+json");
				con.connect();
				
				response.responseCode = con.getResponseCode();
				getResponseHeaders(con.getHeaderFields(), response);					

				if (response.responseCode != expectedResponse) {
					return new HttpResponseException(getErrorMessage(con));
				} else {			
				
					is = con.getInputStream();
					String jsonString = getJsonString(is);					
						
					JasonParser parser = new JasonParser();
					folders = parser.parseFolderList(jsonString);

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
			appInterface.onFoldersReceived(folders, response);
		}
	}

	//Testing
	public FolderNetworkProvider() {}
}
