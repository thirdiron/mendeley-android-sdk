package com.mendeley.api.network;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;

import android.os.AsyncTask;
import android.util.Log;

import com.mendeley.api.exceptions.HttpResponseException;
import com.mendeley.api.exceptions.JsonParsingException;
import com.mendeley.api.exceptions.MendeleyException;
import com.mendeley.api.model.Document;
import com.mendeley.api.model.Folder;
import com.mendeley.api.network.DocumentNetworkProvider.PatchDocumentTask;
import com.mendeley.api.network.NetworkProvider.HttpPatch;
import com.mendeley.api.network.components.MendeleyResponse;
import com.mendeley.api.network.interfaces.MendeleyFolderInterface;

/**
 * NetworkProvider class for Folder API calls
 * 
 * @author Elad
 *
 */
public class FolderNetworkProvider extends NetworkProvider{
	
	private static String foldersUrl = apiUrl + "folders/";
	MendeleyFolderInterface appInterface;
	
	FolderNetworkProvider(MendeleyFolderInterface appInterface) {
		this.appInterface = appInterface;
	}
	
	/**
	 *  Building the url string with the parameters and executes the GetFoldersTask
	 * 
	 * @param fileId
	 */
	protected void doGetFolders(String groupId) {
		String url = foldersUrl;
		if (groupId != null && !groupId.isEmpty()) {
			url += "?group_id="+groupId;
		}
		
		new GetFoldersTask().execute(url);		  
	}

	/**
	 *  Building the url string with the parameters and executes the GetFolderTask
	 * 
	 * @param fileId
	 */
	protected void doGetFolder(String folderId) {
		new GetFolderTask().execute(foldersUrl+folderId);		  
	}
	

	/**
	 *  Building the url string with the parameters and executes the GetFolderDocumentIdsTask
	 * 
	 * @param fileId
	 */
	protected void doGetFolderDocumentIds(String folderId) {
		String url = foldersUrl + folderId + "/documents";
		new GetFolderDocumentIdsTask().execute(url);		  
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
	 * Building the url string with the parameters and executes the PostDocumentToFolderTask.
	 * 
	 * @param folderId the folder id
	 * @param documentId the id of the document to add to the folder
	 */
	protected void doPostDocumentToFolder(String folderId, String documentId) {
		String url = foldersUrl + folderId + "/documents";
		String documentString = null;
		if (documentId != null && !documentId.isEmpty()) {
			JasonParser parser = new JasonParser();
			try {
				documentString = parser.jsonFromDocumentId(documentId);
			} catch (JSONException e) {
				appInterface.onAPICallFail(new JsonParsingException(e.getMessage()));
			}
		}	
		new PostDocumentToFolderTask().execute(url, documentString, folderId);	
	}
	
	/**
	 * Building the url string with the parameters and executes the DeleteFolderTask.
	 * 
	 * @param folderId the id of the folder to delete
	 */
	protected void doDeleteFolder(String folderId) {
		new DeleteFolderTask().execute(foldersUrl+folderId, folderId);	
	}
	
	/**
	 * Building the url string with the parameters and executes the DeleteDocumentFromFolderTask.
	 * 
	 * @param folderId the id of the folder
	 * @param documentId the id of the document to delete
	 */
	protected void doDeleteDocumentFromFolder(String folderId, String documentId) {
		String url = foldersUrl+folderId+"/documents"+documentId;
		
		new DeleteDocumentFromFolderTask().execute(url, documentId);	
	}
	
	/**
	 * Building the url string with the parameters and executes the PatchDocumentTask.
	 * 
	 * @param documentId the document id to be patched
	 * @param date the date object
	 * @param document the Document to patch
	 */
	protected void doPatchFolder(String folderId, Folder folder) {
		JasonParser parser = new JasonParser();
		String folderString = null;
		try {
			folderString  = parser.jsonFromFolder(folder);
		} catch (JSONException e) {
			appInterface.onAPICallFail(new JsonParsingException(e.getMessage()));
		}
		
		new PatchFolderTask().execute(foldersUrl+folderId, folderId, folderString);	
	}
	
	/**
	 * Executing the api call for patching a folder in the background.
	 * Calling the appropriate JsonParser method to parse the json string to objects 
	 * and send the data to the relevant callback method in the MendeleyFolderInterface.
	 * If the call response code is different than expected or an exception is being thrown in the process
	 * creates a new MendeleyException with the relevant information which will be passed to the application via the callback.
	 */
	protected class PatchFolderTask extends AsyncTask<String, Void, MendeleyException> {

		MendeleyResponse response;
		String folderId = null;
		int expectedResult = 200;
		
		@Override
		protected MendeleyException doInBackground(String... params) {
			
			String url = params[0];
			String id = params[1];
			String jsonString = params[2];

			HttpClient httpclient = new DefaultHttpClient();
			HttpPatch httpPatch = getHttpPatch(url);

	        try {
	        	
	        	httpPatch.setEntity(new StringEntity(jsonString));
	        	HttpResponse response = httpclient.execute(httpPatch);				
				int responseCode = response.getStatusLine().getStatusCode();	        	
				
				if (responseCode != expectedResult) {
					return new HttpResponseException("Response code: " + responseCode);
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
			appInterface.onFolderPatched(folderId, result);
		}
	}
	
	/**
	 * Executing the api call for deleting a document from folder in the background.
	 * sending the data to the relevant callback method in the MendeleyFolderInterface.
	 * If the call response code is different than expected or an exception is being thrown in the process
	 * creates a new MendeleyException with the relevant information which will be passed to the application via the callback.
	 */
	protected class DeleteDocumentFromFolderTask extends AsyncTask<String, Void, MendeleyException> {

		String documentId = null;
		MendeleyResponse response;
		int expectedResponse = 204;
		
		@Override
		protected MendeleyException doInBackground(String... params) {
			
			String url = params[0];
			String id = params[1];
			
			HttpsURLConnection con = null;
			try {
				con = getConnection(url, "DELETE");
				con.connect();
				
				response = getResponse(con);

				if (response.responseCode != expectedResponse) {
					return new HttpResponseException("Response code: " + response.responseCode);
				} else {
					
					documentId = id;
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
			appInterface.onFolderDocumentDeleted(documentId, result);
		}
	}
	
	/**
	 * Executing the api call for deleting a folder in the background.
	 * sending the data to the relevant callback method in the MendeleyFolderInterface.
	 * If the call response code is different than expected or an exception is being thrown in the process
	 * creates a new MendeleyException with the relevant information which will be passed to the application via the callback.
	 */
	protected class DeleteFolderTask extends AsyncTask<String, Void, MendeleyException> {

		String folderId = null;
		MendeleyResponse response;
		int expectedResponse = 204;
		
		@Override
		protected MendeleyException doInBackground(String... params) {
			
			String url = params[0];
			String id = params[1];
			
			HttpsURLConnection con = null;
			try {
				con = getConnection(url, "DELETE");
				con.connect();
				
				response = getResponse(con);

				if (response.responseCode != expectedResponse) {
					return new HttpResponseException("Response code: " + response.responseCode);
				} else {
					folderId = id;
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
			appInterface.onFolderDeleted(folderId, result);
		}
	}
	
	/**
	 * Executing the api call for posting a document to a folder in the background.
	 * sending the data to the relevant callback method in the MendeleyFolderInterface.
	 * If the call response code is different than expected or an exception is being thrown in the process
	 * creates a new MendeleyException with the relevant information which will be passed to the application via the callback.
	 */
	protected class PostDocumentToFolderTask extends AsyncTask<String, Void, MendeleyException> {

		String folderId;
		MendeleyResponse response;
		int expectedResponse = 201;
		
		@Override
		protected MendeleyException doInBackground(String... params) {
			
			String url = params[0];
			String jsonString = params[1];

			HttpsURLConnection con = null;

			OutputStream os = null;
			
			Log.e("", "url: " + url);
			Log.e("", "jsonString: " + jsonString);
			
			try {
				con = getConnection(url, "POST");				
				con.connect();
	
				os = con.getOutputStream();
				BufferedWriter writer = new BufferedWriter(
				        new OutputStreamWriter(os, "UTF-8")); 
				writer.write(jsonString);
				writer.flush();
				writer.close();
				os.close();
				
				response = getResponse(con);

				if (response.responseCode != expectedResponse) {
					return new HttpResponseException("Response code: " + response.responseCode);
				} else {

					folderId = params[2];
					return null;
				}
				
			}	catch (IOException e) {
				return new JsonParsingException(e.getMessage());
			} finally {
				if (os != null) {
					try {
						os.close();
						os = null;
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
			appInterface.onDocumentPostedToFolder(folderId, result);
		}
	}
	
	/**
	 * Executing the api call for posting a folder in the background.
	 * sending the data to the relevant callback method in the MendeleyFolderInterface.
	 * If the call response code is different than expected or an exception is being thrown in the process
	 * creates a new MendeleyException with the relevant information which will be passed to the application via the callback.
	 */
	protected class PostFolderTask extends AsyncTask<String, Void, MendeleyException> {

		Folder folder;
		MendeleyResponse response;
		int expectedResponse = 201;
		
		@Override
		protected MendeleyException doInBackground(String... params) {
			
			String url = params[0];
			String jsonString = params[1];

			HttpsURLConnection con = null;

			InputStream is = null;
			OutputStream os = null;
			
			Log.e("", url);
			Log.e("", jsonString);
			
			try {
				con = getConnection(url, "POST");				
				con.connect();
	
				os = con.getOutputStream();
				BufferedWriter writer = new BufferedWriter(
				        new OutputStreamWriter(os, "UTF-8")); 
				writer.write(jsonString);
				writer.flush();
				writer.close();
				os.close();
				
				response = getResponse(con);

				if (response.responseCode != expectedResponse) {
					return new HttpResponseException("Response code: " + response.responseCode);
				} else {

					is = con.getInputStream();
					String responseString = getJsonString(is);					
					is.close();
					
					JasonParser parser = new JasonParser();
					folder = parser.parseFolder(responseString);
					
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
				if (os != null) {
					try {
						os.close();
						os = null;
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
			appInterface.onFolderPosted(folder, result);
		}
	}
	
	/**
	 * Executing the api call for getting folders in the background.
	 * Calling the appropriate JsonParser method to parse the json string to object
	 * and send the data to the relevant callback method in the MendeleyFolderInterface.
	 * If the call response code is different than expected or an exception is being thrown in the process
	 * creates a new MendeleyException with the relevant information which will be passed to the application via the callback.
	 */
	protected class GetFolderDocumentIdsTask extends AsyncTask<String, Void, MendeleyException> {

		List<String> documentIds;
		MendeleyResponse response = null;
		int expectedResponse = 200;
		InputStream is = null;

		@Override
		protected MendeleyException doInBackground(String... params) {

			String url = params[0];

			HttpsURLConnection con = null;

			try {
				con = getConnection(url, "GET");
				con.connect();
				
				response = getResponse(con);				

				if (response.responseCode != expectedResponse) {
					return new HttpResponseException("Response code: " + response.responseCode);
				} else {			
				
					is = con.getInputStream();
					String jsonString = getJsonString(is);					
					is.close();

					JasonParser parser = new JasonParser();
					documentIds = parser.parseDocumentIds(jsonString);

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
				}	
			}
		}
		
		@Override
		protected void onPostExecute(MendeleyException result) {			
			appInterface.onFolderDocumentIdsReceived(documentIds, result);
		}
	}
	
	/**
	 * Executing the api call for getting a folder in the background.
	 * Calling the appropriate JsonParser method to parse the json string to object
	 * and send the data to the relevant callback method in the MendeleyFolderInterface.
	 * If the call response code is different than expected or an exception is being thrown in the process
	 * creates a new MendeleyException with the relevant information which will be passed to the application via the callback.
	 */
	protected class GetFolderTask extends AsyncTask<String, Void, MendeleyException> {

		Folder folder;
		MendeleyResponse response = null;
		int expectedResponse = 200;
		InputStream is = null;

		@Override
		protected MendeleyException doInBackground(String... params) {

			String url = params[0];

			HttpsURLConnection con = null;

			try {
				con = getConnection(url, "GET");
				con.connect();
				
				response = getResponse(con);				

				if (response.responseCode != expectedResponse) {
					return new HttpResponseException("Response code: " + response.responseCode);
				} else {			
				
					is = con.getInputStream();
					String jsonString = getJsonString(is);					
					is.close();
			
						
					JasonParser parser = new JasonParser();
					folder = parser.parseFolder(jsonString);

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
				}	
			}
		}
		
		@Override
		protected void onPostExecute(MendeleyException result) {			
			appInterface.onFolderReceived(folder, result);
		}
	}
	
	/**
	 * Executing the api call for getting folders in the background.
	 * Calling the appropriate JsonParser method to parse the json string to object
	 * and send the data to the relevant callback method in the MendeleyFolderInterface.
	 * If the call response code is different than expected or an exception is being thrown in the process
	 * creates a new MendeleyException with the relevant information which will be passed to the application via the callback.
	 */
	protected class GetFoldersTask extends AsyncTask<String, Void, MendeleyException> {

		List<Folder> folders;
		MendeleyResponse response = null;
		int expectedResponse = 200;
		InputStream is = null;

		@Override
		protected MendeleyException doInBackground(String... params) {

			String url = params[0];

			HttpsURLConnection con = null;

			try {
				con = getConnection(url, "GET");
				con.connect();
				
				response = getResponse(con);				

				if (response.responseCode != expectedResponse) {
					return new HttpResponseException("Response code: " + response.responseCode);
				} else {			
				
					is = con.getInputStream();
					String jsonString = getJsonString(is);					
					is.close();
			
						
					JasonParser parser = new JasonParser();
					folders = parser.parseFolderList(jsonString);

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
				}	
			}
		}
		
		@Override
		protected void onPostExecute(MendeleyException result) {			
			appInterface.onFoldersReceived(folders, result);
		}
	}

}
