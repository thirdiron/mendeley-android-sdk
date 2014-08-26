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

import com.mendeley.api.auth.AccessTokenProvider;
import com.mendeley.api.callbacks.RequestHandle;
import com.mendeley.api.callbacks.folder.DeleteFolderCallback;
import com.mendeley.api.callbacks.folder.DeleteFolderDocumentCallback;
import com.mendeley.api.callbacks.folder.GetFolderCallback;
import com.mendeley.api.callbacks.folder.GetFolderDocumentIdsCallback;
import com.mendeley.api.callbacks.folder.GetFoldersCallback;
import com.mendeley.api.callbacks.folder.PatchFolderCallback;
import com.mendeley.api.callbacks.folder.PostDocumentToFolderCallback;
import com.mendeley.api.callbacks.folder.PostFolderCallback;
import com.mendeley.api.exceptions.HttpResponseException;
import com.mendeley.api.exceptions.JsonParsingException;
import com.mendeley.api.exceptions.MendeleyException;
import com.mendeley.api.exceptions.NoMorePagesException;
import com.mendeley.api.exceptions.UserCancelledException;
import com.mendeley.api.model.DocumentId;
import com.mendeley.api.model.Folder;
import com.mendeley.api.params.FolderRequestParameters;
import com.mendeley.api.params.Page;

import static com.mendeley.api.network.NetworkUtils.*;

/**
 * NetworkProvider class for Folder API calls
 */
public class FolderNetworkProvider {
	private static String foldersUrl = API_URL + "folders";

    private final Environment environment;
    private final AccessTokenProvider accessTokenProvider;

    public FolderNetworkProvider(Environment environment, AccessTokenProvider accessTokenProvider) {
        this.environment = environment;
        this.accessTokenProvider = accessTokenProvider;
    }

	/**
	 * Getting the appropriate url string and executes the GetFoldersTask
	 * 
	 * @param params folder request parameters object
	 */
    public RequestHandle doGetFolders(FolderRequestParameters params, GetFoldersCallback callback) {
		String[] paramsArray = new String[]{getGetFoldersUrl(params)};
        GetFoldersTask getFoldersTask = new GetFoldersTask(callback);
        getFoldersTask.executeOnExecutor(environment.getExecutor(), paramsArray);
        return getFoldersTask;
	}

    /**
     * Getting the appropriate url string and executes the GetFoldersTask
     *
     * @param next reference to next page
     */
    public RequestHandle doGetFolders(Page next, GetFoldersCallback callback) {
        if (Page.isValidPage(next)) {
    		String[] paramsArray = new String[]{next.link};
            GetFoldersTask getFoldersTask = new GetFoldersTask(callback);
            new GetFoldersTask(callback).executeOnExecutor(environment.getExecutor(), paramsArray);
            return getFoldersTask;
        } else {
            callback.onFoldersNotReceived(new NoMorePagesException());
            return NullRequest.get();
        }
    }

	/**
	 * Getting the appropriate url string and executes the GetFolderTask
	 * 
	 * @param folderId the folder id to get
	 */
    public void doGetFolder(String folderId, GetFolderCallback callback) {
		String[] paramsArray = new String[]{getGetFolderUrl(folderId)};			
		new GetFolderTask(callback).executeOnExecutor(environment.getExecutor(), paramsArray);
	}

	/**
	 * Getting the appropriate url string and executes the GetFolderDocumentIdsTask
	 * 
	 * @param folderId the folder id
	 */
    public void doGetFolderDocumentIds(FolderRequestParameters params, String folderId, GetFolderDocumentIdsCallback callback) {
		String[] paramsArray = new String[]{getGetFoldersUrl(params, getGetFolderDocumentIdsUrl(folderId)), folderId};			
		new GetFolderDocumentIdsTask(callback).executeOnExecutor(environment.getExecutor(), paramsArray);
	}

    /**
     * Getting the appropriate url string and executes the GetFolderDocumentIdsTask
     *
     * @param next reference to next page
     */
    public void doGetFolderDocumentIds(Page next, String folderId, GetFolderDocumentIdsCallback callback) {
        if (Page.isValidPage(next)) {
    		String[] paramsArray = new String[]{next.link, folderId};			
            new GetFolderDocumentIdsTask(callback).executeOnExecutor(environment.getExecutor(), paramsArray);
        } else {
            callback.onFolderDocumentIdsNotReceived(new NoMorePagesException());
        }
    }

    /**
	 * Building the url string with the parameters and executes the PostFolderTask.
	 * 
	 * @param folder the folder to post
	 */
    public void doPostFolder(Folder folder, PostFolderCallback callback) {

		JsonParser parser = new JsonParser();
		try {
    		String[] paramsArray = new String[]{foldersUrl, parser.jsonFromFolder(folder)};			
			new PostFolderTask(callback).executeOnExecutor(environment.getExecutor(), paramsArray);
		} catch (JSONException e) {
            callback.onFolderNotPosted(new JsonParsingException(e.getMessage()));
        }
	}

    /**
     * Getting the appropriate url string and executes the PatchFolderTask.
     *
     * @param folderId the folder id to patch
     * @param folder the Folder object
     */
    public void doPatchFolder(String folderId, Folder folder, PatchFolderCallback callback) {
        JsonParser parser = new JsonParser();
        String folderString = null;
        try {
            folderString  = parser.jsonFromFolder(folder);
        } catch (JSONException e) {
            callback.onFolderNotPatched(new JsonParsingException(e.getMessage()));
        }
        String[] paramsArray = new String[]{getPatchFolderUrl(folderId), folderId, folderString};
        new PatchFolderTask(callback).executeOnExecutor(environment.getExecutor(), paramsArray);
    }

    /**
     * Getting the appropriate url string and executes the DeleteFolderTask.
     *
     * @param folderId the id of the folder to delete
     */
    public void doDeleteFolder(String folderId, DeleteFolderCallback callback) {
        String[] paramsArray = new String[] { getDeleteFolderUrl(folderId) };
        new DeleteFolderTask(folderId, callback).executeOnExecutor(environment.getExecutor(), paramsArray);
    }

    /**
     * Getting the appropriate url string and executes the PostDocumentToFolderTask.
     *
     * @param folderId the folder id
     * @param documentId the id of the document to add to the folder
     */
    public void doPostDocumentToFolder(String folderId, String documentId, PostDocumentToFolderCallback callback) {
        String documentString = null;
        if (documentId != null && !documentId.isEmpty()) {
            JsonParser parser = new JsonParser();
            try {
                documentString = parser.jsonFromDocumentId(documentId);
            } catch (JSONException e) {
                callback.onDocumentNotPostedToFolder(new JsonParsingException(e.getMessage()));
            }
        }
        String[] paramsArray = new String[]{getPostDocumentToFolderUrl(folderId), documentString, folderId};
        new PostDocumentToFolderTask(callback).executeOnExecutor(environment.getExecutor(), paramsArray);
    }

    /**
     * Getting the appropriate url string and executes the DeleteDocumentFromFolderTask.
     *
     * @param folderId the id of the folder
     * @param documentId the id of the document to delete
     */
    public void doDeleteDocumentFromFolder(String folderId, String documentId, DeleteFolderDocumentCallback callback) {
        String[] paramsArray = new String[] { getDeleteDocumentFromFolderUrl(folderId, documentId) };
        new DeleteDocumentFromFolderTask(documentId, callback).executeOnExecutor(environment.getExecutor(), paramsArray);
    }

    /* URLS */

    /**
     * Building the url for get folders
     *
     * @param params folder request parameters object
     * @return the url string
     */
    String getGetFoldersUrl(FolderRequestParameters params, String requestUrl) {
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
        }

        return url.toString();
    }

    String getGetFoldersUrl(FolderRequestParameters params) {
        return getGetFoldersUrl(params, null);
    }

    /**
     * Building the url for get folder
     *
     * @param folderId the folder id to get
     * @return the url string
     */
    String getGetFolderUrl(String folderId) {
        return foldersUrl+"/"+folderId;
    }

    /**
     * Building the url for patch folder
     *
     * @param folderId the folder id to patch
     * @return the url string
     */
    String getPatchFolderUrl(String folderId) {
        return foldersUrl + "/"+folderId;
    }

    /**
	 * Building the url for delete folder
	 * 
	 * @param folderId the folder id
	 * @return the url string
	 */
    String getDeleteFolderUrl(String folderId) {
		return foldersUrl + "/"+folderId;
	}

    /**
     * Building the url for get folder document ids
     *
     * @param folderId the folder id
     * @return the url string
     */
    String getGetFolderDocumentIdsUrl(String folderId) {
        return foldersUrl + "/"+folderId + "/documents";
    }

    /**
     * Building the url for post document to folder
     *
     * @param folderId the folder id
     * @return the url string
     */
    String getPostDocumentToFolderUrl(String folderId) {
        return foldersUrl + "/"+folderId + "/documents";
    }

    /**
	 * Building the url for delete document from folder
	 * 
	 * @param folderId the id of the folder
	 * @param documentId the id of the document to delete
	 */
    String getDeleteDocumentFromFolderUrl(String folderId, String documentId) {
		return foldersUrl+"/"+folderId+"/documents"+documentId;
	}
	
    /* TASKS */

    private class GetFoldersTask extends GetNetworkTask {
        private final GetFoldersCallback callback;

        List<Folder> folders;

        private GetFoldersTask(GetFoldersCallback callback) {
            this.callback = callback;
        }

        @Override
        protected void processJsonString(String jsonString) throws JSONException {
            folders = JsonParser.parseFolderList(jsonString);
        }

        @Override
        protected String getContentType() {
            return "application/vnd.mendeley-folder.1+json";
        }

        @Override
        protected void onCancelled (MendeleyException result) {
            callback.onFoldersNotReceived(new UserCancelledException());
        }

        @Override
        protected AccessTokenProvider getAccessTokenProvider() {
            return accessTokenProvider;
        }

        @Override
        protected void onSuccess() {
            callback.onFoldersReceived(folders, next);
        }

        @Override
        protected void onFailure(MendeleyException exception) {
            callback.onFoldersNotReceived(exception);
        }
    }

    private class GetFolderTask extends GetNetworkTask {
        private final GetFolderCallback callback;

        Folder folder;

        private GetFolderTask(GetFolderCallback callback) {
            this.callback = callback;
        }

        @Override
        protected void processJsonString(String jsonString) throws JSONException {
            folder = JsonParser.parseFolder(jsonString);
        }

        @Override
        protected String getContentType() {
            return "application/vnd.mendeley-folder.1+json";
        }

        @Override
        protected AccessTokenProvider getAccessTokenProvider() {
            return accessTokenProvider;
        }

        @Override
        protected void onSuccess() {
            callback.onFolderReceived(folder);
        }

        @Override
        protected void onFailure(MendeleyException exception) {
            callback.onFolderNotReceived(exception);
        }
    }

    /**
     * Executing the api call for posting a folder in the background.
     * sending the data to the relevant callback method in the MendeleyFolderInterface.
     * If the call response code is different than expected or an exception is being thrown in the process
     * the exception will be added to the MendeleyResponse which is passed to the application via the callback.
     */
    private class PostFolderTask extends PostNetworkTask {
        private final PostFolderCallback callback;

        Folder folder;

        private PostFolderTask(PostFolderCallback callback) {
            this.callback = callback;
        }

        @Override
        protected void processJsonString(String jsonString) throws JSONException {
            folder = JsonParser.parseFolder(jsonString);
        }

        @Override
        protected String getContentType() {
            return "application/vnd.mendeley-folder.1+json";
        }

        @Override
        protected AccessTokenProvider getAccessTokenProvider() {
            return accessTokenProvider;
        }

        @Override
        protected void onSuccess() {
            callback.onFolderPosted(folder);
        }

        @Override
        protected void onFailure(MendeleyException exception) {
            callback.onFolderNotPosted(exception);
        }
    }

    /**
	 * Executing the api call for patching a folder in the background.
	 * Calling the appropriate JsonParser method to parse the json string to objects 
	 * and send the data to the relevant callback method in the MendeleyFolderInterface.
	 * If the call response code is different than expected or an exception is being thrown in the process
	 * the exception will be added to the MendeleyResponse which is passed to the application via the callback.
	 */
    private class PatchFolderTask extends NetworkTask {
        private final PatchFolderCallback callback;

		String folderId = null;

        private PatchFolderTask(PatchFolderCallback callback) {
            this.callback = callback;
        }

        @Override
		protected int getExpectedResponse() {
			return 200;
		}

        @Override
        protected AccessTokenProvider getAccessTokenProvider() {
            return accessTokenProvider;
        }

        @Override
		protected MendeleyException doInBackground(String... params) {
			
			String url = params[0];
			String id = params[1];
			String jsonString = params[2];
			
			HttpClient httpclient = new DefaultHttpClient();
			HttpPatch httpPatch = getFolderHttpPatch(url, getAccessTokenProvider());

	        try {
	        	
	        	httpPatch.setEntity(new StringEntity(jsonString));
	        	HttpResponse response = httpclient.execute(httpPatch);

				final int responseCode = response.getStatusLine().getStatusCode();
				if (responseCode != getExpectedResponse()) {
					return new HttpResponseException(responseCode, getErrorMessage(response));
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
			callback.onFolderPatched(folderId);
		}

		@Override
		protected void onFailure(MendeleyException exception) {
			callback.onFolderNotPatched(exception);
		}
	}

    /**
     * Executing the api call for deleting a folder in the background.
     * sending the data to the relevant callback method in the MendeleyFolderInterface.
     * If the call response code is different than expected or an exception is being thrown in the process
     * the exception will be added to the MendeleyResponse which is passed to the application via the callback.
     */
    private class DeleteFolderTask extends DeleteNetworkTask {
        private final String folderId;
        private final DeleteFolderCallback callback;

        private DeleteFolderTask(String folderId, DeleteFolderCallback callback) {
            this.folderId = folderId;
            this.callback = callback;
        }

        @Override
        protected AccessTokenProvider getAccessTokenProvider() {
            return accessTokenProvider;
        }

        @Override
        protected void onSuccess() {
            callback.onFolderDeleted(folderId);
        }

        @Override
        protected void onFailure(MendeleyException exception) {
            callback.onFolderNotDeleted(exception);
        }
    }

    /**
     * Executing the api call for posting a document to a folder in the background.
     * sending the data to the relevant callback method in the MendeleyFolderInterface.
     * If the call response code is different than expected or an exception is being thrown in the process
     * the exception will be added to the MendeleyResponse which is passed to the application via the callback.
     */
    private class PostDocumentToFolderTask extends NetworkTask {
        private final PostDocumentToFolderCallback callback;

        String folderId;

        private PostDocumentToFolderTask(PostDocumentToFolderCallback callback) {
            this.callback = callback;
        }

        @Override
        protected int getExpectedResponse() {
            return 201;
        }

        @Override
        protected AccessTokenProvider getAccessTokenProvider() {
            return accessTokenProvider;
        }

        @Override
        protected MendeleyException doInBackground(String... params) {

            String url = params[0];
            String jsonString = params[1];

            try {
                con = getConnection(url, "POST", getAccessTokenProvider());
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

                final int responseCode = con.getResponseCode();
                if (responseCode != getExpectedResponse()) {
                    return new HttpResponseException(responseCode, getErrorMessage(con));
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
            callback.onDocumentPostedToFolder(folderId);
        }

        @Override
        protected void onFailure(MendeleyException exception) {
            callback.onDocumentNotPostedToFolder(exception);
        }
    }

    /**
	 * Executing the api call for deleting a document from folder in the background.
	 * sending the data to the relevant callback method in the MendeleyFolderInterface.
	 * If the call response code is different than expected or an exception is being thrown in the process
	 * the exception will be added to the MendeleyResponse which is passed to the application via the callback.
	 */
    private class DeleteDocumentFromFolderTask extends DeleteNetworkTask {
		private final String documentId;
        private final DeleteFolderDocumentCallback callback;

        public DeleteDocumentFromFolderTask(String documentId, DeleteFolderDocumentCallback callback) {
            this.documentId = documentId;
            this.callback = callback;
        }

        @Override
        protected AccessTokenProvider getAccessTokenProvider() {
            return accessTokenProvider;
        }

        @Override
		protected void onSuccess() {
			callback.onFolderDocumentDeleted(documentId);
		}

		@Override
		protected void onFailure(MendeleyException exception) {
			callback.onFolderDocumentNotDeleted(exception);
		}
	}
	
	/**
	 * Executing the api call for getting folders in the background.
	 * Calling the appropriate JsonParser method to parse the json string to object
	 * and send the data to the relevant callback method in the MendeleyFolderInterface.
	 * If the call response code is different than expected or an exception is being thrown in the process
	 * the exception will be added to the MendeleyResponse which is passed to the application via the callback.
	 */
    private class GetFolderDocumentIdsTask extends NetworkTask {
        private final GetFolderDocumentIdsCallback callback;

		List<DocumentId> documentIds;
		String folderId;

        private GetFolderDocumentIdsTask(GetFolderDocumentIdsCallback callback) {
            this.callback = callback;
        }

        @Override
		protected int getExpectedResponse() {
			return 200;
		}

        @Override
        protected AccessTokenProvider getAccessTokenProvider() {
            return accessTokenProvider;
        }

        @Override
		protected MendeleyException doInBackground(String... params) {

			String url = params[0];
			if (params.length > 1) {
				folderId = params[1];
			}
			try {
				con = getConnection(url, "GET", getAccessTokenProvider());
				con.addRequestProperty("Content-type", "application/vnd.mendeley-document.1+json");
				con.connect();
				
				getResponseHeaders();

                final int responseCode = con.getResponseCode();
                if (responseCode != getExpectedResponse()) {
					return new HttpResponseException(responseCode, getErrorMessage(con));
				} else {			
				
					is = con.getInputStream();
					String jsonString = getJsonString(is);					

					documentIds = JsonParser.parseDocumentIds(jsonString);

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
			callback.onFolderDocumentIdsReceived(folderId, documentIds, next);
		}

		@Override
		protected void onFailure(MendeleyException exception) {
			callback.onFolderDocumentIdsNotReceived(exception);
		}
	}
}
