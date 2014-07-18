package com.mendeley.api.network;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;

import android.os.AsyncTask;

import com.mendeley.api.callbacks.RequestHandle;
import com.mendeley.api.exceptions.HttpResponseException;
import com.mendeley.api.exceptions.JsonParsingException;
import com.mendeley.api.exceptions.MendeleyException;
import com.mendeley.api.exceptions.NoMorePagesException;
import com.mendeley.api.exceptions.UserCancelledException;
import com.mendeley.api.model.Document;
import com.mendeley.api.params.DocumentRequestParameters;
import com.mendeley.api.params.Page;
import com.mendeley.api.network.interfaces.MendeleyDocumentInterface;

/**
 * NetworkProvider class for Documents API calls
 */
public class DocumentNetworkProvider extends NetworkProvider {
	private static String documentsUrl = apiUrl + "documents";
	
	private static String documentTypesUrl = apiUrl + "document_types";
	
	public static SimpleDateFormat patchDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT' Z");
	
	protected MendeleyDocumentInterface appInterface;
	
	/**
	 * Constructor that takes MendeleyDocumentInterface instance which will be used to send callbacks to the application
	 * 
	 * @param appInterface the instance of MendeleyDocumentInterface
	 */
    public DocumentNetworkProvider(MendeleyDocumentInterface appInterface) {
		this.appInterface = appInterface;
	}

    /**
     * Getting the appropriate url string and executes the GetDocumentsTask.
     *
     * @param params the document request parameters
     */
    public RequestHandle doGetDocuments(DocumentRequestParameters params) {
        try {
            String[] paramsArray = new String[] { getGetDocumentsUrl(params) };
            GetDocumentsTask getDocumentsTask = new GetDocumentsTask();
            getDocumentsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, paramsArray);
            return getDocumentsTask;
        }
        catch (UnsupportedEncodingException e) {
            appInterface.onDocumentsNotReceived(new MendeleyException(e.getMessage()));
            return NullRequest.get();
        }
    }

    /**
     * Getting the appropriate url string and executes the GetDocumentsTask.
     *
     * @param next reference to next page
     */
    public RequestHandle doGetDocuments(Page next) {
        if (Page.isValidPage(next)) {
            String[] paramsArray = new String[]{next.link};
            GetDocumentsTask getDocumentsTask = new GetDocumentsTask();
            getDocumentsTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, paramsArray);
            return getDocumentsTask;
        } else {
            appInterface.onDocumentsNotReceived(new NoMorePagesException());
            return NullRequest.get();
        }
    }

    /**
     * Getting the appropriate url string and executes the GetDocumentTask.
     *
     * @param documentId the document id
     * @param params the document request parameters
     */
    public void doGetDocument(String documentId, DocumentRequestParameters params) {
        String[] paramsArray = new String[]{getGetDocumentUrl(documentId, params)};
        new GetDocumentTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, paramsArray);
    }

	/**
	 * Building the url string with the parameters and executes the PostDocumentTask.
	 * 
	 * @param document the document to post
	 */
    public void doPostDocument(Document document) {

		JsonParser parser = new JsonParser();
		try {
			String[] paramsArray = new String[]{documentsUrl, parser.jsonFromDocument(document)};			
			new PostDocumentTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, paramsArray); 		
		} catch (JSONException e) {
            appInterface.onDocumentNotPosted(new JsonParsingException(e.getMessage()));
        }
	}

    /**
     * Getting the appropriate url string and executes the PatchDocumentTask.
     *
     * @param documentId the document id to be patched
     * @param date the date object
     * @param document the Document to patch
     */
    public void doPatchDocument(String documentId, Date date, Document document) {
        String dateString = null;

        if (date != null) {
            dateString = formatDate(date);
        }

        JsonParser parser = new JsonParser();
        try {
            String[] paramsArray = new String[]{getPatchDocumentUrl(documentId), documentId, dateString, parser.jsonFromDocument(document)};
            new PatchDocumentTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, paramsArray);
        } catch (JSONException e) {
            appInterface.onDocumentNotPatched(new JsonParsingException(e.getMessage()));
        }
    }

    /**
	 * Getting the appropriate url string and executes the PostTrashDocumentTask.
	 * 
	 * @param documentId the document id to trash
	 */
    public void doPostTrashDocument(String documentId) {
		String[] paramsArray = new String[]{getTrashDocumentUrl(documentId), documentId};			
		new PostTrashDocumentTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, paramsArray); 
	}

    /**
     * Getting the appropriate url string and executes the DeleteDocumentTask.
     *
     * @param documentId the document if to delete
     */
    public void doDeleteDocument(String documentId) {
        String[] paramsArray = new String[]{getDeleteDocumentUrl(documentId), documentId};
        new DeleteDocumentTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, paramsArray);
    }

    /**
     * Getting the appropriate url string and executes the GetDocumentTypesTask.
     */
    public RequestHandle doGetDocumentTypes() {
        String[] paramsArray = new String[] { documentTypesUrl };
        GetDocumentTypesTask getDocumentTypesTask = new GetDocumentTypesTask();
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
			} else {
				paramsString.append(firstParam?"?":"&").append("view=all");
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

    /**
     * Executing the api call for posting a document in the background.
     * sending the data to the relevant callback method in the MendeleyDocumentInterface.
     * If the call response code is different than expected or an exception is being thrown in the process
     * the exception will be added to the MendeleyResponse which is passed to the application via the callback.
     */
    private class GetDocumentsTask extends NetworkTask {
        List<Document> documents;

        @Override
        protected int getExpectedResponse() {
            return 200;
        }

        @Override
        protected MendeleyException doInBackground(String... params) {
            String url = params[0];

            try {
                con = getConnection(url, "GET");
                con.addRequestProperty("Content-type", "application/vnd.mendeley-document.1+json");
                con.connect();

                getResponseHeaders();

                if (con.getResponseCode() != 200) {
                    return new HttpResponseException(getErrorMessage(con));
                } else if (!isCancelled()) {

                    is = con.getInputStream();
                    String jsonString = getJsonString(is);

                    documents = JsonParser.parseDocumentList(jsonString);

                    return null;
                } else {
                    return new UserCancelledException();
                }

            }	catch (IOException | JSONException e) {
                return new JsonParsingException(e.getMessage());
            } finally {
                closeConnection();
            }
        }

        @Override
        protected void onCancelled (MendeleyException result) {
            appInterface.onDocumentsNotReceived(new UserCancelledException());
        }

        @Override
        protected void onSuccess() {
            appInterface.onDocumentsReceived(documents, next);
        }

        @Override
        protected void onFailure(MendeleyException exception) {
            appInterface.onDocumentsNotReceived(exception);
        }
    }

    /**
     * Executing the api call for getting a document in the background.
     * sending the data to the relevant callback method in the MendeleyDocumentInterface.
     * If the call response code is different than expected or an exception is being thrown in the process
     * the exception will be added to the MendeleyResponse which is passed to the application via the callback.
     */
    private class GetDocumentTask extends NetworkTask {
        Document document;
        String date;

        @Override
        protected int getExpectedResponse() {
            return 200;
        }

        @Override
        protected MendeleyException doInBackground(String... params) {

            String url = params[0];

            try {
                con = getConnection(url, "GET");
                con.addRequestProperty("Content-type", "application/vnd.mendeley-document.1+json");
                con.connect();

                getResponseHeaders();

                if (con.getResponseCode() != getExpectedResponse()) {
                    return new HttpResponseException(getErrorMessage(con));
                } else {

                    is = con.getInputStream();
                    String jsonString = getJsonString(is);

                    document = JsonParser.parseDocument(jsonString);

                    return null;
                }

            } catch (IOException | JSONException e) {
                return new JsonParsingException(e.getMessage());
            } finally {
                closeConnection();
            }
        }

        @Override
        protected void onSuccess() {
            appInterface.onDocumentReceived(document);
        }

        @Override
        protected void onFailure(MendeleyException exception) {
            appInterface.onDocumentNotReceived(exception);
        }
    }

    /**
     * Executing the api call for posting a document in the background.
     * sending the data to the relevant callback method in the MendeleyDocumentInterface.
     * If the call response code is different than expected or an exception is being thrown in the process
     * the exception will be added to the MendeleyResponse which is passed to the application via the callback.
     */
    private class PostDocumentTask extends NetworkTask {
        Document document;

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
            appInterface.onDocumentPosted(document);
        }

        @Override
        protected void onFailure(MendeleyException exception) {
            appInterface.onDocumentNotPosted(exception);
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
		String documentId = null;
		
		@Override
		protected int getExpectedResponse() {
			return 204;
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
			appInterface.onDocumentPatched(documentId);
		}

		@Override
		protected void onFailure(MendeleyException exception) {
			appInterface.onDocumentNotPatched(exception);
		}
	}
	
	/**
	 * Executing the api call for posting trash document in the background.
	 * sending the data to the relevant callback method in the MendeleyDocumentInterface.
	 * If the call response code is different than expected or an exception is being thrown in the process
	 * the exception will be added to the MendeleyResponse which is passed to the application via the callback.
	 */
	private class PostTrashDocumentTask extends NetworkTask {
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
			appInterface.onDocumentTrashed(documentId);
		}

		@Override
		protected void onFailure(MendeleyException exception) {
			appInterface.onDocumentNotTrashed(exception);
		}
	}

    /**
     * Executing the api call for deleting a document in the background.
     * sending the data to the relevant callback method in the MendeleyDocumentInterface.
     * If the call response code is different than expected or an exception is being thrown in the process
     * the exception will be added to the MendeleyResponse which is passed to the application via the callback.
     */
    private class DeleteDocumentTask extends NetworkTask {
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
            appInterface.onDocumentDeleted(documentId);
        }

        @Override
        protected void onFailure(MendeleyException exception) {
            appInterface.onDocumentNotDeleted(exception);
        }
    }

	/**
	 * Executing the api call for getting a document types in the background.
	 * sending the data to the relevant callback method in the MendeleyDocumentInterface.
	 * If the call response code is different than expected or an exception is being thrown in the process
	 * the exception will be added to the MendeleyResponse which is passed to the application via the callback.
	 */
	private class GetDocumentTypesTask extends NetworkTask {
		Map<String, String> typesMap;
		String date;
		
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
	    	appInterface.onDocumentTypesNotReceived(new UserCancelledException());
	    }
		
		@Override
		protected void onSuccess() {
			appInterface.onDocumentTypesReceived(typesMap);
		}

		@Override
		protected void onFailure(MendeleyException exception) {
			appInterface.onDocumentTypesNotReceived(exception);
		}
	}

	//TESTING
	public DocumentNetworkProvider() {}
}
