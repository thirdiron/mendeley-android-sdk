package com.mendeley.api.network;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.mendeley.api.exceptions.HttpResponseException;
import com.mendeley.api.exceptions.JsonParsingException;
import com.mendeley.api.exceptions.MendeleyException;
import com.mendeley.api.model.Document;
import com.mendeley.api.network.components.DocumentRequestParameters;
import com.mendeley.api.network.components.MendeleyResponse;
import com.mendeley.api.network.interfaces.MendeleyDocumentInterface;

/**
 * NetworkProvider class for Documents API calls
 * 
 */
public class DocumentNetworkProvider extends NetworkProvider {

	private static String documentsUrl = apiUrl + "documents";
	
	public static SimpleDateFormat patchDateFormat =  new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT' Z");

	protected MendeleyDocumentInterface appInterface;
	
	/**
	 * Constructor that takes MendeleyDocumentInterface instance which will be used to send callbacks to the application
	 * 
	 * @param appInterface the instance of MendeleyDocumentInterface
	 */
	protected DocumentNetworkProvider(MendeleyDocumentInterface appInterface) {
		this.appInterface = appInterface;
	}
	
	/**
	 * Building the url for deleting document
	 * 
	 * @param documentId the id of the document to delete
	 * @return the url string
	 */
	protected String getDeleteDocumentUrl(String documentId) {
		return documentsUrl + "/"+documentId;
	}
	
	/**
	 * Getting the appropriate url string and executes the DeleteDocumentTask.
	 *  
	 * @param documentId the document if to delete
	 */
	protected void doDeleteDocument(String documentId) {
		 new DeleteDocumentTask().execute(getDeleteDocumentUrl(documentId), documentId);
	}

	/**
	 * Building the url string with the parameters and executes the PostDocumentTask.
	 * 
	 * @param document the document to post
	 */
	protected void doPostDocument(Document document) {

		JasonParser parser = new JasonParser();
		try {
			new PostDocumentTask().execute(documentsUrl, parser.jsonFromDocument(document));			
		} catch (JSONException e) {
			appInterface.onAPICallFail(new JsonParsingException(e.getMessage()));
		}
	}
	
	/**
	 * Building the url for post trash document
	 * 
	 * @param documentId the id of the document to trash
	 * @return the url string
	 */
	protected String getTrashDocumentUrl(String documentId) {
		return documentsUrl + "/" + documentId + "/trash";
	}
	
	/**
	 * Getting the appropriate url string and executes the PostTrashDocumentTask.
	 * 
	 * @param documentId the document id to trash
	 */
	protected void doPostTrashDocument(String documentId) {
		 new PostTrashDocumentTask().execute(getTrashDocumentUrl(documentId), documentId);
	}
	
	/**
	 * Building the url for get document
	 * 
	 * @param documentId the document id
	 * @param params the document request parameters
	 * @return the url string
	 */
	protected String getGetDocumentUrl(String documentId, DocumentRequestParameters params) {
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
	 * Getting the appropriate url string and executes the GetDocumentTask.
	 * 
	 * @param documentId the document id
	 * @param params the document request parameters
	 */
	protected void doGetDocument(String documentId, DocumentRequestParameters params) {
		new GetDocumentTask().execute(getGetDocumentUrl(documentId, params));
	}
	
	/**
	 * Building the url for get documents
	 * 
	 * @param params the document request parameters
	 * @return the url string
	 * @throws UnsupportedEncodingException 
	 */
	protected String getGetDocumentsUrl(DocumentRequestParameters params) throws UnsupportedEncodingException {
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
	 * Getting the appropriate url string and executes the GetDocumentsTask.
	 * 
	 * @param params the document request parameters
	 */
	protected void doGetDocuments(DocumentRequestParameters params) {
		try {
			new GetDocumentsTask().execute(getGetDocumentsUrl(params));		  
		}
		catch (UnsupportedEncodingException e) {
			appInterface.onAPICallFail(new MendeleyException(e.getMessage()));
		}
	}
	
	/**
	 * private method for formating date
	 * 
	 * @param date the date to format
	 * @return date string in the specified format
	 */
	private String formatDate(Date date) {       
        return patchDateFormat.format(date);
	}
	
	/**
	 * Building the url for patch document
	 * 
	 * @param documentId the id of the document to patch
	 * @return the url string
	 */
	protected String getPatchDocumentUrl(String documentId) {
		return documentsUrl + "/" + documentId;
	}

	/**
	 * Getting the appropriate url string and executes the PatchDocumentTask.
	 * 
	 * @param documentId the document id to be patched
	 * @param date the date object
	 * @param document the Document to patch
	 */
	protected void doPatchDocument(String documentId, Date date, Document document) {

		String dateString = null;
		
		if (date != null) {
			dateString = formatDate(date);
		}
		
		JasonParser parser = new JasonParser();
		try {
			document.title += " patched!";
			new PatchDocumentTask().execute(getPatchDocumentUrl(documentId), documentId, dateString, parser.jsonFromDocument(document));		
		} catch (JSONException e) {
			appInterface.onAPICallFail(new JsonParsingException(e.getMessage()));
		}
	}
	
	/**
	 * Executing the api call for patching a document in the background.
	 * Calling the appropriate JsonParser method to parse the json string to objects 
	 * and send the data to the relevant callback method in the MendeleyDocumentInterface.
	 * If the call response code is different than expected or an exception is being thrown in the process
	 * the exception will be added to the MendeleyResponse which is passed to the application via the callback.
	 */
	protected class PatchDocumentTask extends NetworkTask {

		String documentId = null;
		
		@Override
		protected void onPreExecute() {
			expectedResponse = 204;
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
				
				if (responseCode != expectedResponse) {
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
		protected void onPostExecute(MendeleyException result) {
			super.onPostExecute(result);
			appInterface.onDocumentPatched(documentId, response);
		}
	}
	
	/**
	 * Executing the api call for deleting a document in the background.
	 * sending the data to the relevant callback method in the MendeleyDocumentInterface.
	 * If the call response code is different than expected or an exception is being thrown in the process
	 * the exception will be added to the MendeleyResponse which is passed to the application via the callback.
	 */
	protected class DeleteDocumentTask extends NetworkTask {

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
			appInterface.onDocumentDeleted(documentId, response);
		}
	}
	
	/**
	 * Executing the api call for posting trash document in the background.
	 * sending the data to the relevant callback method in the MendeleyDocumentInterface.
	 * If the call response code is different than expected or an exception is being thrown in the process
	 * the exception will be added to the MendeleyResponse which is passed to the application via the callback.
	 */
	protected class PostTrashDocumentTask extends NetworkTask {

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
				con = getConnection(url, "POST");
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
			appInterface.onDocumentTrashed(documentId, response);
		}
	}
	
	/**
	 * Executing the api call for posting a document in the background.
	 * sending the data to the relevant callback method in the MendeleyDocumentInterface.
	 * If the call response code is different than expected or an exception is being thrown in the process
	 * the exception will be added to the MendeleyResponse which is passed to the application via the callback.
	 */
	protected class PostDocumentTask extends NetworkTask {

		Document document;
		
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
				con.addRequestProperty("Content-type", "application/vnd.mendeley-document.1+json"); 

				os = con.getOutputStream();
				BufferedWriter writer = new BufferedWriter(
				        new OutputStreamWriter(os, "UTF-8"));
				writer.write(jsonString);
				writer.flush();
				writer.close();
				os.close();
				
				con.connect();
				
				response.responseCode = con.getResponseCode();
				getResponseHeaders(con.getHeaderFields(), response);	

				if (response.responseCode != expectedResponse) {
					return new HttpResponseException(getErrorMessage(con));
				} else {

					is = con.getInputStream();
					String responseString = getJsonString(is);					
					
					JasonParser parser = new JasonParser();
					document = parser.parseDocument(responseString);
					
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
			appInterface.onDocumentPosted(document, response);
		}
	}
	
	/**
	 * Executing the api call for getting a document in the background.
	 * sending the data to the relevant callback method in the MendeleyDocumentInterface.
	 * If the call response code is different than expected or an exception is being thrown in the process
	 * the exception will be added to the MendeleyResponse which is passed to the application via the callback.
	 */
	protected class GetDocumentTask extends NetworkTask {

		Document document;
		String date;
		
		@Override
		protected void onPreExecute() {
			expectedResponse = 200;
		}
		
		@Override
		protected MendeleyException doInBackground(String... params) {

				String url = params[0];

				try {
					con = getConnection(url, "GET");
					con.addRequestProperty("Content-type", "application/vnd.mendeley-document.1+json"); 
					con.connect();

					response.responseCode = con.getResponseCode();
					getResponseHeaders(con.getHeaderFields(), response);	
					
					if (response.responseCode != expectedResponse) {
						return new HttpResponseException(getErrorMessage(con));
					} else {

						is = con.getInputStream();
						String jsonString = getJsonString(is);					

						JasonParser parser = new JasonParser();
						document = parser.parseDocument(jsonString);
						
						return null;
					}
					
				} catch (IOException | JSONException e) {
					return new JsonParsingException(e.getMessage());
				} finally {
					closeConnection();
				}
		}
		
		@Override
		protected void onPostExecute(MendeleyException result) {
			super.onPostExecute(result);
			appInterface.onDocumentReceived(document, response);
		}
	}


	/**
	 * Executing the api call for posting a document in the background.
	 * sending the data to the relevant callback method in the MendeleyDocumentInterface.
	 * If the call response code is different than expected or an exception is being thrown in the process
	 * the exception will be added to the MendeleyResponse which is passed to the application via the callback.
	 */
	protected class GetDocumentsTask extends NetworkTask {

		List<Document> documents;

		@Override
		protected void onPreExecute() {
			expectedResponse = 200;
		}
		
		@Override
		protected MendeleyException doInBackground(String... params) {

			String url = params[0];
			
			try {
				con = getConnection(url, "GET");
				con.addRequestProperty("Content-type", "application/vnd.mendeley-document.1+json"); 
				con.connect();
				
				response.responseCode = con.getResponseCode();
				getResponseHeaders(con.getHeaderFields(), response);	
				
				if (response.responseCode != 200) {
					return new HttpResponseException(getErrorMessage(con));
				} else {			
				
					is = con.getInputStream();
					String jsonString = getJsonString(is);					

					JasonParser parser = new JasonParser();
					documents = parser.parseDocumentList(jsonString);
					
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
			appInterface.onDocumentsReceived(documents, response);			
		}
	}
	
	//testing
	
	public DocumentNetworkProvider() {}
	
	
}
