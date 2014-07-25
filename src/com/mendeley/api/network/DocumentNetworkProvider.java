package com.mendeley.api.network;

import android.os.AsyncTask;

import com.mendeley.api.callbacks.RequestHandle;
import com.mendeley.api.callbacks.document.DeleteDocumentCallback;
import com.mendeley.api.callbacks.document.GetDeletedDocumentsCallback;
import com.mendeley.api.callbacks.document.GetDocumentCallback;
import com.mendeley.api.callbacks.document.GetDocumentTypesCallback;
import com.mendeley.api.callbacks.document.GetDocumentsCallback;
import com.mendeley.api.callbacks.document.PatchDocumentCallback;
import com.mendeley.api.callbacks.document.PostDocumentCallback;
import com.mendeley.api.callbacks.document.TrashDocumentCallback;
import com.mendeley.api.exceptions.HttpResponseException;
import com.mendeley.api.exceptions.JsonParsingException;
import com.mendeley.api.exceptions.MendeleyException;
import com.mendeley.api.exceptions.NoMorePagesException;
import com.mendeley.api.exceptions.UserCancelledException;
import com.mendeley.api.model.Document;
import com.mendeley.api.model.DocumentId;
import com.mendeley.api.params.DocumentRequestParameters;
import com.mendeley.api.params.Page;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.mendeley.api.network.NetworkUtils.API_URL;
import static com.mendeley.api.network.NetworkUtils.HttpPatch;
import static com.mendeley.api.network.NetworkUtils.getConnection;
import static com.mendeley.api.network.NetworkUtils.getErrorMessage;
import static com.mendeley.api.network.NetworkUtils.getHttpPatch;
import static com.mendeley.api.network.NetworkUtils.getJsonString;

/**
 * NetworkProvider class for Documents API calls
 */
public class DocumentNetworkProvider extends NetworkProvider {
	private static String documentsUrl = API_URL + "documents";
	
	private static String documentTypesUrl = API_URL + "document_types";
	
	public static SimpleDateFormat patchDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT' Z");
	
    /**
     * Getting the appropriate url string and executes the GetDocumentsTask.
     *
     * @param params the document request parameters
     */
    public RequestHandle doGetDocuments(DocumentRequestParameters params, GetDocumentsCallback callback) {
        try {
            String[] paramsArray = new String[] { getGetDocumentsUrl(params) };
            GetDocumentsTask getDocumentsTask = new GetDocumentsTask(callback);
            getDocumentsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, paramsArray);
            return getDocumentsTask;
        }
        catch (UnsupportedEncodingException e) {
            callback.onDocumentsNotReceived(new MendeleyException(e.getMessage()));
            return NullRequest.get();
        }
    }

    /**
     * Getting the appropriate url string and executes the GetDeletedDocumentsTask.
     *
     * @param params the document request parameters
     */
    public RequestHandle doGetDeletedDocuments(DocumentRequestParameters params, GetDeletedDocumentsCallback callback) {
        if (params.deletedSince == null) {
            callback.onDeletedDocumentsNotReceived(new MendeleyException("deletedSince cannot be null"));
        }
        try {
            String[] paramsArray = new String[] { getGetDocumentsUrl(params) };
            GetDeletedDocumentsTask getDocumentsTask = new GetDeletedDocumentsTask(callback);
            getDocumentsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, paramsArray);
            return getDocumentsTask;
        }
        catch (UnsupportedEncodingException e) {
            callback.onDeletedDocumentsNotReceived(new MendeleyException(e.getMessage()));
            return NullRequest.get();
        }
    }

    /**
     * Getting the appropriate url string and executes the GetDocumentsTask.
     *
     * @param next reference to next page
     */
    public RequestHandle doGetDocuments(Page next, GetDocumentsCallback callback) {
        if (Page.isValidPage(next)) {
            String[] paramsArray = new String[]{next.link};
            GetDocumentsTask getDocumentsTask = new GetDocumentsTask(callback);
            getDocumentsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, paramsArray);
            return getDocumentsTask;
        } else {
            callback.onDocumentsNotReceived(new NoMorePagesException());
            return NullRequest.get();
        }
    }


    /**
     * Getting the appropriate url string and executes the GetDeletedDocumentsTask.
     *
     * @param next reference to next page
     */
    public RequestHandle doGetDeletedDocuments(Page next, GetDeletedDocumentsCallback callback) {
        if (Page.isValidPage(next)) {
            String[] paramsArray = new String[]{next.link};
            GetDeletedDocumentsTask getDocumentsTask = new GetDeletedDocumentsTask(callback);
            getDocumentsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, paramsArray);
            return getDocumentsTask;
        } else {
            callback.onDeletedDocumentsNotReceived(new NoMorePagesException());
            return NullRequest.get();
        }
    }

    /**
     * Getting the appropriate url string and executes the GetDocumentTask.
     *
     * @param documentId the document id
     * @param params the document request parameters
     */
    public void doGetDocument(String documentId, DocumentRequestParameters params, GetDocumentCallback callback) {
        String[] paramsArray = new String[]{getGetDocumentUrl(documentId, params)};
        new GetDocumentTask(callback).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, paramsArray);
    }

	/**
	 * Building the url string with the parameters and executes the PostDocumentTask.
	 * 
	 * @param document the document to post
	 */
    public void doPostDocument(Document document, PostDocumentCallback callback) {
		JsonParser parser = new JsonParser();
		try {
			String[] paramsArray = new String[]{documentsUrl, parser.jsonFromDocument(document)};			
			new PostDocumentTask(callback).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, paramsArray);
		} catch (JSONException e) {
            callback.onDocumentNotPosted(new JsonParsingException(e.getMessage()));
        }
	}

    /**
     * Getting the appropriate url string and executes the PatchDocumentTask.
     *
     * @param documentId the document id to be patched
     * @param date the date object
     * @param document the Document to patch
     */
    public void doPatchDocument(String documentId, Date date, Document document, PatchDocumentCallback callback) {
        String dateString = null;

        if (date != null) {
            dateString = formatDate(date);
        }

        JsonParser parser = new JsonParser();
        try {
            String[] paramsArray = new String[]{getPatchDocumentUrl(documentId), documentId, dateString, parser.jsonFromDocument(document)};
            new PatchDocumentTask(callback).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, paramsArray);
        } catch (JSONException e) {
            callback.onDocumentNotPatched(new JsonParsingException(e.getMessage()));
        }
    }

    /**
	 * Getting the appropriate url string and executes the PostTrashDocumentTask.
	 * 
	 * @param documentId the document id to trash
	 */
    public void doPostTrashDocument(String documentId, TrashDocumentCallback callback) {
		String[] paramsArray = new String[]{getTrashDocumentUrl(documentId), documentId};			
		new PostTrashDocumentTask(callback).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, paramsArray);
	}

    /**
     * Getting the appropriate url string and executes the DeleteDocumentTask.
     *
     * @param documentId the document if to delete
     */
    public void doDeleteDocument(String documentId, DeleteDocumentCallback callback) {
        String[] paramsArray = new String[] { getDeleteDocumentUrl(documentId) };
        new DeleteDocumentTask(documentId, callback).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, paramsArray);
    }

    /**
     * Getting the appropriate url string and executes the GetDocumentTypesTask.
     */
    public RequestHandle doGetDocumentTypes(GetDocumentTypesCallback callback) {
        String[] paramsArray = new String[] { documentTypesUrl };
        GetDocumentTypesTask getDocumentTypesTask = new GetDocumentTypesTask(callback);
        getDocumentTypesTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, paramsArray);
        return getDocumentTypesTask;
    }

    /* URLS */

    /**
     * Building the url for deleting document
     *
     * @param documentId the id of the document to delete
     * @return the url string
     */
    String getDeleteDocumentUrl(String documentId) {
        return documentsUrl + "/"+documentId;
    }

    /**
     * Building the url for post trash document
     *
     * @param documentId the id of the document to trash
     * @return the url string
     */
    String getTrashDocumentUrl(String documentId) {
        return documentsUrl + "/" + documentId + "/trash";
    }

    /**
     * Building the url for get document
     *
     * @param documentId the document id
     * @param params the document request parameters
     * @return the url string
     */
    String getGetDocumentUrl(String documentId, DocumentRequestParameters params) {
        StringBuilder url = new StringBuilder();
        url.append(documentsUrl);
        url.append("/").append(documentId);

        if (params != null) {
            if (params.view != null) {
                url.append("?").append("view="+params.view);
            }
        }

        return url.toString();
    }

    /**
	 * Building the url for get documents
	 * 
	 * @param params the document request parameters
	 * @return the url string
	 * @throws UnsupportedEncodingException 
	 */
	String getGetDocumentsUrl(DocumentRequestParameters params) throws UnsupportedEncodingException {
		StringBuilder url = new StringBuilder();
		url.append(documentsUrl);
		StringBuilder paramsString = new StringBuilder();
		
		if (params != null) {
			boolean firstParam = true;		
			if (params.view != null) {
				paramsString.append(firstParam?"?":"&").append("view="+params.view);
				firstParam = false;
			}
			if (params.groupId != null) {
				paramsString.append(firstParam?"?":"&").append("group_id="+params.groupId);
				firstParam = false;
			}
			if (params.modifiedSince != null) {
				paramsString.append(firstParam?"?":"&").append("modified_since="+URLEncoder.encode(params.modifiedSince, "ISO-8859-1"));
				firstParam = false;
			}
			if (params.deletedSince != null) {
				paramsString.append(firstParam?"?":"&").append("deleted_since="+URLEncoder.encode(params.deletedSince, "ISO-8859-1"));
				firstParam = false;
			} else if (params.view == null) {
				paramsString.append(firstParam?"?":"&").append("view=all");
				firstParam = false;
            }
			if (params.limit != null) {
				paramsString.append(firstParam?"?":"&").append("limit="+params.limit);
				firstParam = false;
			}
			if (params.marker != null) {
				paramsString.append(firstParam?"?":"&").append("marker="+params.marker);
				firstParam = false;
			}
			if (params.reverse != null) {
				paramsString.append(firstParam?"?":"&").append("reverse="+params.reverse);
				firstParam = false;
			}
			if (params.order != null) {
				paramsString.append(firstParam?"?":"&").append("order="+params.order);
				firstParam = false;
			}
			if (params.sort != null) {
				paramsString.append(firstParam?"?":"&").append("sort="+params.sort);
			}
		}
		
		url.append(paramsString.toString());
		return url.toString();
	}
	
	/**
	 * Building the url for patch document
	 * 
	 * @param documentId the id of the document to patch
	 * @return the url string
	 */
	String getPatchDocumentUrl(String documentId) {
		return documentsUrl + "/" + documentId;
	}

    /**
     * @param date the date to format
     * @return date string in the specified format
     */
    private String formatDate(Date date) {
        return patchDateFormat.format(date);
    }

    /* TASKS */

    private class GetDocumentsTask extends GetNetworkTask {
        private final GetDocumentsCallback callback;

        List<Document> documents;

        private GetDocumentsTask(GetDocumentsCallback callback) {
            this.callback = callback;
        }

        @Override
        protected void processJsonString(String jsonString) throws JSONException {
            documents = JsonParser.parseDocumentList(jsonString);
        }

        @Override
        protected String getContentType() {
            return "application/vnd.mendeley-document.1+json";
        }

        @Override
        protected void onCancelled (MendeleyException result) {
            callback.onDocumentsNotReceived(new UserCancelledException());
        }

        @Override
        protected void onSuccess() {
            callback.onDocumentsReceived(documents, next, serverDate);
        }

        @Override
        protected void onFailure(MendeleyException exception) {
            callback.onDocumentsNotReceived(exception);
        }
    }

    private class GetDeletedDocumentsTask extends GetNetworkTask {
        private final GetDeletedDocumentsCallback callback;

        List<DocumentId> documentIds;

        private GetDeletedDocumentsTask(GetDeletedDocumentsCallback callback) {
            this.callback = callback;
        }

        @Override
        protected void processJsonString(String jsonString) throws JSONException {
            documentIds = JsonParser.parseDocumentIds(jsonString);
        }

        @Override
        protected String getContentType() {
            return "application/vnd.mendeley-document.1+json";
        }

        @Override
        protected void onCancelled (MendeleyException result) {
            callback.onDeletedDocumentsNotReceived(new UserCancelledException());
        }

        @Override
        protected void onSuccess() {
            callback.onDeletedDocumentsReceived(documentIds, next, serverDate);
        }

        @Override
        protected void onFailure(MendeleyException exception) {
            callback.onDeletedDocumentsNotReceived(exception);
        }
    }

    private class GetDocumentTask extends GetNetworkTask {
        private final GetDocumentCallback callback;

        Document document;

        private GetDocumentTask(GetDocumentCallback callback) {
            this.callback = callback;
        }

        @Override
        protected void processJsonString(String jsonString) throws JSONException {
            document = JsonParser.parseDocument(jsonString);
        }

        @Override
        protected String getContentType() {
            return "application/vnd.mendeley-document.1+json";
        }

        @Override
        protected void onSuccess() {
            callback.onDocumentReceived(document);
        }

        @Override
        protected void onFailure(MendeleyException exception) {
            callback.onDocumentNotReceived(exception);
        }
    }

    /**
     * Executing the api call for posting a document in the background.
     * sending the data to the relevant callback method in the MendeleyDocumentInterface.
     * If the call response code is different than expected or an exception is being thrown in the process
     * the exception will be added to the MendeleyResponse which is passed to the application via the callback.
     */
    private class PostDocumentTask extends NetworkTask {
        private final PostDocumentCallback callback;

        Document document;

        private PostDocumentTask(PostDocumentCallback callback) {
            this.callback = callback;
        }

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
                con.addRequestProperty("Content-type", "application/vnd.mendeley-document.1+json");
                con.setFixedLengthStreamingMode(jsonString.getBytes().length);

                os = con.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(jsonString);
                writer.flush();
                writer.close();
                os.close();

                con.connect();

                getResponseHeaders();

                if (con.getResponseCode() != getExpectedResponse()) {
                    return new HttpResponseException(getErrorMessage(con));
                } else {

                    is = con.getInputStream();
                    String responseString = getJsonString(is);

                    document = JsonParser.parseDocument(responseString);

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
            callback.onDocumentPosted(document);
        }

        @Override
        protected void onFailure(MendeleyException exception) {
            callback.onDocumentNotPosted(exception);
        }
    }

    /**
	 * Executing the api call for patching a document in the background.
	 * Calling the appropriate JsonParser method to parse the json string to objects 
	 * and send the data to the relevant callback method in the MendeleyDocumentInterface.
	 * If the call response code is different than expected or an exception is being thrown in the process
	 * the exception will be added to the MendeleyResponse which is passed to the application via the callback.
	 */
	private class PatchDocumentTask extends NetworkTask {
        private final PatchDocumentCallback callback;

		String documentId = null;

        private PatchDocumentTask(PatchDocumentCallback callback) {
            this.callback = callback;
        }

        @Override
		protected int getExpectedResponse() {
			return 200;
		}
		
		@Override
		protected MendeleyException doInBackground(String... params) {
			String url = params[0];
			String id = params[1];
			String date = params[2];
			String jsonString = params[3];

			HttpClient httpclient = new DefaultHttpClient();
			HttpPatch httpPatch = getHttpPatch(url, date); 

	        try {
	        	httpPatch.setEntity(new StringEntity(jsonString));
	        	HttpResponse response = httpclient.execute(httpPatch);				
				int responseCode = response.getStatusLine().getStatusCode();	        	
				
				if (responseCode != getExpectedResponse()) {
					return new HttpResponseException(getErrorMessage(response));
				} else {
					documentId = id;
					return null;
				}
			} catch (IOException e) {
				return new JsonParsingException(e.getMessage());
			} finally {
				closeConnection();
			}
		}
		
		@Override
		protected void onSuccess() {
			callback.onDocumentPatched(documentId);
		}

		@Override
		protected void onFailure(MendeleyException exception) {
			callback.onDocumentNotPatched(exception);
		}
	}
	
	/**
	 * Executing the api call for posting trash document in the background.
	 * sending the data to the relevant callback method in the MendeleyDocumentInterface.
	 * If the call response code is different than expected or an exception is being thrown in the process
	 * the exception will be added to the MendeleyResponse which is passed to the application via the callback.
	 */
	private class PostTrashDocumentTask extends NetworkTask {
        private final TrashDocumentCallback callback;

		String documentId = null;

        private PostTrashDocumentTask(TrashDocumentCallback callback) {
            this.callback = callback;
        }

        @Override
		protected int getExpectedResponse() {
			return 204;
		}
		
		@Override
		protected MendeleyException doInBackground(String... params) {
			String url = params[0];
			String id = params[1];

			try {
				con = getConnection(url, "POST");
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
			callback.onDocumentTrashed(documentId);
		}

		@Override
		protected void onFailure(MendeleyException exception) {
			callback.onDocumentNotTrashed(exception);
		}
	}

    /**
     * Executing the api call for deleting a document in the background.
     * sending the data to the relevant callback method in the MendeleyDocumentInterface.
     * If the call response code is different than expected or an exception is being thrown in the process
     * the exception will be added to the MendeleyResponse which is passed to the application via the callback.
     */
    private class DeleteDocumentTask extends DeleteNetworkTask {
        private final String documentId;
        private final DeleteDocumentCallback callback;

        public DeleteDocumentTask(String documentId, DeleteDocumentCallback callback) {
            super();
            this.callback = callback;
            this.documentId = documentId;
        }

        @Override
        protected void onSuccess() {
            callback.onDocumentDeleted(documentId);
        }

        @Override
        protected void onFailure(MendeleyException exception) {
            callback.onDocumentNotDeleted(exception);
        }
    }

	/**
	 * Executing the api call for getting a document types in the background.
	 * sending the data to the relevant callback method in the MendeleyDocumentInterface.
	 * If the call response code is different than expected or an exception is being thrown in the process
	 * the exception will be added to the MendeleyResponse which is passed to the application via the callback.
	 */
	private class GetDocumentTypesTask extends NetworkTask {
        private final GetDocumentTypesCallback callback;

		Map<String, String> typesMap;
		String date;

        private GetDocumentTypesTask(GetDocumentTypesCallback callback) {
            this.callback = callback;
        }

        @Override
		protected int getExpectedResponse() {
			return 200;
		}
		
		@Override
		protected MendeleyException doInBackground(String... params) {
				String url = params[0];

				try {
					con = getConnection(url, "GET");
					con.addRequestProperty("Content-type", "application/vnd.mendeley-document-type.1+json"); 
					con.connect();

					getResponseHeaders();
					
					if (con.getResponseCode() != getExpectedResponse()) {
						return new HttpResponseException(getErrorMessage(con));
					} else if (!isCancelled()) {

						is = con.getInputStream();
						String jsonString = getJsonString(is);					

						typesMap = JsonParser.parseDocumentTypes(jsonString);
						
						return null;
					} else {
						return new UserCancelledException();
					}
					
				} catch (IOException | JSONException e) {
					return new JsonParsingException(e.getMessage());
				} finally {
					closeConnection();
				}
		}
		
	    @Override
	    protected void onCancelled (MendeleyException result) {
	    	callback.onDocumentTypesNotReceived(new UserCancelledException());
	    }
		
		@Override
		protected void onSuccess() {
			callback.onDocumentTypesReceived(typesMap);
		}

		@Override
		protected void onFailure(MendeleyException exception) {
			callback.onDocumentTypesNotReceived(exception);
		}
	}

	//TESTING
	public DocumentNetworkProvider() {}
}
