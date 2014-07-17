package com.mendeley.api.network;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

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
import com.mendeley.api.exceptions.NoMorePagesException;
import com.mendeley.api.model.DocumentId;
import com.mendeley.api.model.Folder;
import com.mendeley.api.params.FolderRequestParameters;
import com.mendeley.api.params.Page;
import com.mendeley.api.network.interfaces.MendeleyFolderInterface;

/**
 * NetworkProvider class for Folder API calls
 *
 */
public class FolderNetworkProvider extends NetworkProvider{
	
	private static String foldersUrl = apiUrl + "folders";
	MendeleyFolderInterface appInterface;
	
	private GetFoldersTask getFoldersTask;
	
	/**
	 * Constructor that takes MendeleyFolderInterface instance which will be used to send callbacks to the application
	 * 
	 * @param appInterface the instance of MendeleyFolderInterface
	 */
    public FolderNetworkProvider(MendeleyFolderInterface appInterface) {
		this.appInterface = appInterface;
	}
	
	/**
	 * Building the url for get folders
	 * 
	 * @param params folder request parameters object
	 * @return the url string
	 */
	protected String getGetFoldersUrl(FolderRequestParameters params, String requestUrl) {
		StringBuilder url = new StringBuilder();

		url.append(requestUrl==null?foldersUrl:requestUrl);
		
		if (params != null) {
			boolean firstParam = true;
			if (params.groupId != null) {
				url.append(firstParam?"?":"&").append("group_id="+params.groupId);
				firstParam = false;
			}
			if (params.limit != null) {
				url.append(firstParam?"?":"&").append("limit="+params.limit);
				firstParam = false;
			}
			if (params.marker != null) {
				url.append(firstParam?"?":"&").append("marker="+params.marker);
				firstParam = false;
			}
		}
		
		return url.toString();
	}
	
	protected String getGetFoldersUrl(FolderRequestParameters params) {
		return getGetFoldersUrl(params, null);
	}
	
	/**
	 * Cancelling GetFoldersTask if it is currently running
	 */
    public void cancelGetFolders() {
		if (getFoldersTask != null) {
			getFoldersTask.cancel(true);
		}
	}
	

	/**
	 * Getting the appropriate url string and executes the GetFoldersTask
	 * 
	 * @param params folder request parameters object
	 */
    public void doGetFolders(FolderRequestParameters params) {
		getFoldersTask = new GetFoldersTask();		
		String[] paramsArray = new String[]{getGetFoldersUrl(params)};			
		getFoldersTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, paramsArray); 
	}

    /**
     * Getting the appropriate url string and executes the GetFoldersTask
     *
     * @param next reference to next page
     */
    public void doGetFolders(Page next) {
        if (Page.isValidPage(next)) {
    		String[] paramsArray = new String[]{next.link};			
            new GetFoldersTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, paramsArray); 
        } else {
            appInterface.onFoldersNotReceived(new NoMorePagesException());
        }
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
    public void doGetFolder(String folderId) {
		String[] paramsArray = new String[]{getGetFolderUrl(folderId)};			
		new GetFolderTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, paramsArray); 
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
    public void doGetFolderDocumentIds(FolderRequestParameters params, String folderId) {
		String[] paramsArray = new String[]{getGetFoldersUrl(params, getGetFolderDocumentIdsUrl(folderId)), folderId};			
		new GetFolderDocumentIdsTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, paramsArray);   
	}

    /**
     * Getting the appropriate url string and executes the GetFolderDocumentIdsTask
     *
     * @param next reference to next page
     */
    public void doGetFolderDocumentIds(Page next, String folderId) {
        if (Page.isValidPage(next)) {
    		String[] paramsArray = new String[]{next.link, folderId};			
            new GetFolderDocumentIdsTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, paramsArray); 
        } else {
            appInterface.onFolderDocumentIdsNotReceived(new NoMorePagesException());
        }
    }

    /**
	 * Building the url string with the parameters and executes the PostFolderTask.
	 * 
	 * @param folder the folder to post
	 */
    public void doPostFolder(Folder folder) {

		JsonParser parser = new JsonParser();
		try {
    		String[] paramsArray = new String[]{foldersUrl, parser.jsonFromFolder(folder)};			
			new PostFolderTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, paramsArray); 
		} catch (JSONException e) {
            appInterface.onFolderNotPosted(new JsonParsingException(e.getMessage()));
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
    public void doPostDocumentToFolder(String folderId, String documentId) {
		String documentString = null;
		if (documentId != null && !documentId.isEmpty()) {
			JsonParser parser = new JsonParser();
			try {
				documentString = parser.jsonFromDocumentId(documentId);
			} catch (JSONException e) {
                appInterface.onDocumentNotPostedToFolder(new JsonParsingException(e.getMessage()));
            }
		}	
		String[] paramsArray = new String[]{getPostDocumentToFolderUrl(folderId), documentString, folderId};			
		new PostDocumentToFolderTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, paramsArray); 
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
    public void doDeleteFolder(String folderId) {
		String[] paramsArray = new String[]{getDeleteFolderUrl(folderId), folderId};			
		new DeleteFolderTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, paramsArray); 
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
    public void doDeleteDocumentFromFolder(String folderId, String documentId) {
		String[] paramsArray = new String[]{getDeleteDocumentFromFolderUrl(folderId, documentId), documentId};			
		new DeleteDocumentFromFolderTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, paramsArray); 
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
    public void doPatchFolder(String folderId, Folder folder) {
		JsonParser parser = new JsonParser();
		String folderString = null;
		try {
			folderString  = parser.jsonFromFolder(folder);
		} catch (JSONException e) {
            appInterface.onFolderNotPatched(new JsonParsingException(e.getMessage()));
        }
		String[] paramsArray = new String[]{getPatchFolderUrl(folderId), folderId, folderString};			
		new PatchFolderTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, paramsArray); 
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
		protected int getExpectedResponse() {
			return 200;
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
				
				if (responseCode != getExpectedResponse()) {
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
		protected void onSuccess() {
			appInterface.onFolderPatched(folderId);
		}

		@Override
		protected void onFailure(MendeleyException exception) {
			appInterface.onFolderNotPatched(exception);
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
		protected void onSuccess() {
			appInterface.onFolderDocumentDeleted(documentId);
		}

		@Override
		protected void onFailure(MendeleyException exception) {
			appInterface.onFolderDocumentNotDeleted(exception);
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
		protected void onSuccess() {
			appInterface.onFolderDeleted(folderId);
		}

		@Override
		protected void onFailure(MendeleyException exception) {
			appInterface.onFolderNotDeleted(exception);
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
		protected int getExpectedResponse() {
			return 201;
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
				
				getResponseHeaders();

				if (con.getResponseCode() != getExpectedResponse()) {
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
		protected void onSuccess() {
			appInterface.onDocumentPostedToFolder(folderId);
		}

		@Override
		protected void onFailure(MendeleyException exception) {
			appInterface.onDocumentNotPostedToFolder(exception);
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
		protected int getExpectedResponse() {
			return 201;
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
				
				getResponseHeaders();

				if (con.getResponseCode() != getExpectedResponse()) {
					return new HttpResponseException(getErrorMessage(con));
				} else {

					is = con.getInputStream();
					String responseString = getJsonString(is);					

					JsonParser parser = new JsonParser();
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
		protected void onSuccess() {
			appInterface.onFolderPosted(folder);
		}

		@Override
		protected void onFailure(MendeleyException exception) {
			appInterface.onFolderNotPosted(exception);
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

		List<DocumentId> documentIds;
		String folderId;

		@Override
		protected int getExpectedResponse() {
			return 200;
		}
		
		@Override
		protected MendeleyException doInBackground(String... params) {

			String url = params[0];
			if (params.length > 1) {
				folderId = params[1];
			}
			try {
				con = getConnection(url, "GET");
				con.addRequestProperty("Content-type", "application/vnd.mendeley-folder-documentids.1+json");
				con.connect();
				
				getResponseHeaders();

				if (con.getResponseCode() != getExpectedResponse()) {
					return new HttpResponseException(getErrorMessage(con));
				} else {			
				
					is = con.getInputStream();
					String jsonString = getJsonString(is);					

					JsonParser parser = new JsonParser();
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
		protected void onSuccess() {
			appInterface.onFolderDocumentIdsReceived(folderId, documentIds, next);
		}

		@Override
		protected void onFailure(MendeleyException exception) {
			appInterface.onFolderDocumentIdsNotReceived(exception);
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
		protected int getExpectedResponse() {
			return 200;
		}
		
		@Override
		protected MendeleyException doInBackground(String... params) {

			String url = params[0];

			try {
				con = getConnection(url, "GET");
				con.addRequestProperty("Content-type", "application/vnd.mendeley-folder.1+json");
				con.connect();
				
				getResponseHeaders();

				if (con.getResponseCode() != getExpectedResponse()) {
					return new HttpResponseException(getErrorMessage(con));
				} else {			
				
					is = con.getInputStream();
					String jsonString = getJsonString(is);					

					JsonParser parser = new JsonParser();
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
		protected void onSuccess() {
			appInterface.onFolderReceived(folder);
		}

		@Override
		protected void onFailure(MendeleyException exception) {
			appInterface.onFolderNotReceived(exception);
		}
	}
	
	/**
	 * Executing the api call for getting folders in the background.
	 * Calling the appropriate JsonParser method to parse the json string to object
	 * If the call response code is different than expected or an exception is being thrown in the process
	 * creates a new MendeleyException with the relevant information which will be passed to the application via the callback.
	 */
	protected class GetFoldersTask extends NetworkTask {

		List<Folder> folders;

		@Override
		protected int getExpectedResponse() {
			return 200;
		}
		
		@Override
		protected MendeleyException doInBackground(String... params) {

			String url = params[0];
			
			try {
				con = getConnection(url, "GET");
				con.addRequestProperty("Content-type", "application/vnd.mendeley-folder.1+json");
				con.connect();
				
				getResponseHeaders();

				if (con.getResponseCode() != getExpectedResponse()) {
					return new HttpResponseException(getErrorMessage(con));
				} else if (!isCancelled()) {				
				
					is = con.getInputStream();
					String jsonString = getJsonString(is);					
						
					JsonParser parser = new JsonParser();
					folders = parser.parseFolderList(jsonString);

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
	    	appInterface.onFoldersNotReceived(new MendeleyException("Operation cancelled by the user"));	
	    	getFoldersTask = null;
	    }
	    
		@Override
		protected void onSuccess() {
			appInterface.onFoldersReceived(folders, next);
		}

		@Override
		protected void onFailure(MendeleyException exception) {
			appInterface.onFoldersNotReceived(exception);
		}
	}

	//Testing
	public FolderNetworkProvider() {}
}
