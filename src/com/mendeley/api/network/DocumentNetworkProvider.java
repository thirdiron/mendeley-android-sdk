package com.mendeley.api.network;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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
import com.mendeley.api.network.components.DocumentRequestParameters;
import com.mendeley.api.network.components.MendeleyResponse;
import com.mendeley.api.network.interfaces.MendeleyDocumentInterface;

/**
 * NetworkProvider class for Documents API calls
 * 
 * @author Elad
 *
 */
public class DocumentNetworkProvider extends NetworkProvider {

	private static String documentsUrl = apiUrl + "documents/";
	
	public static int documentsLimit = 3;

	protected MendeleyDocumentInterface appInterface;
	
	protected DocumentNetworkProvider(MendeleyDocumentInterface appInterface) {
		this.appInterface = appInterface;
	}
	
	/**
	 *  Building the url string with the parameters and executes the DeleteDocumentTask.
	 *  
	 * @param documentId the document if to delete
	 */
	protected void doDeleteDocument(String documentId) {
		 new DeleteDocumentTask().execute(documentsUrl + documentId, documentId);
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
			Log.e("", "", e);
		}
	}
	
	/**
	 * Building the url string with the parameters and executes the PostTrashDocumentTask.
	 * 
	 * @param documentId the document id to trash
	 */
	protected void doPostTrashDocument(String documentId) {
		 new PostTrashDocumentTask().execute(documentsUrl + documentId + "/trash", documentId);
	}
	
	/**
	 * Building the url string with the parameters and executes the GetDocumentTask.
	 * 
	 * @param documentId the document id to trash
	 */
	protected void doGetDocument(String documentId, DocumentRequestParameters params) {
		StringBuilder url = new StringBuilder();
		url.append(documentsUrl);
		url.append(documentId);
		
		if (params != null) {
			if (params.view != null) {
				url.append("?").append("view="+params.view);
			}
		}

		new GetDocumentTask().execute(url.toString());
	}
	
	/**
	 * Building the url string with the parameters and executes the GetDocumentsTask.
	 * 
	 * @param documentId the document id to trash
	 */
	protected void doGetDocuments(DocumentRequestParameters params) {
		StringBuilder url = new StringBuilder();
		url.append(documentsUrl);
		
		if (params != null) {
			boolean firstParam = true;		
			if (params.view != null) {
				url.append(firstParam?"?":"&").append("view="+params.view);
				firstParam = false;
			}
			if (params.groupId != null) {
				url.append(firstParam?"?":"&").append("group_id="+params.groupId);
				firstParam = false;
			}
			if (params.modifiedSince != null) {
				url.append(firstParam?"?":"&").append("modified_since="+params.modifiedSince);
				firstParam = false;
			}
			if (params.deletedSince != null) {
				url.append(firstParam?"?":"&").append("deleted_since="+params.deletedSince);
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
			if (params.reverse != null) {
				url.append(firstParam?"?":"&").append("reverse="+params.reverse);
				firstParam = false;
			}
			if (params.order != null) {
				url.append(firstParam?"?":"&").append("order="+params.order);
				firstParam = false;
			}
			if (params.sort != null) {
				url.append(firstParam?"?":"&").append("sort="+params.sort);
			}
		}
		
		new GetDocumentsTask().execute(url.toString());		  
	}
	
	/**
	 * private method for formating date
	 * 
	 * @param date the date to format
	 * @return date string in the specified format
	 */
	private String formatDate(Date date) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        return df.format(date);
	}
	
	
	/**
	 * Building the url string with the parameters and executes the PatchDocumentTask.
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
			new PatchDocumentTask().execute(documentsUrl+documentId, documentId, dateString, parser.jsonFromDocument(document));
			
		} catch (JSONException e) {
			Log.e("", "", e);
		}
	}
	
	/**
	 * Executing the api call for patching a document in the background.
	 * Calling the appropriate JsonParser method to parse the json string to objects 
	 * and send the data to the relevant callback method in the MendeleyDocumentInterface.
	 * If the call response code is different than expected or an exception is being thrown in the process
	 * creates a new MendeleyException with the relevant information which will be passed to the application via the callback.
	 */
	protected class PatchDocumentTask extends AsyncTask<String, Void, MendeleyException> {

		MendeleyResponse response;
		String documentId = null;
		int expectedResult = 204;
		
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
				
				if (responseCode != expectedResult) {
					return new HttpResponseException("Response code: " + responseCode);
				} else {
					
					documentId = id;
					return null;
				}
			} catch (IOException e) {
				return new JsonParsingException(e.getMessage());
			}
			
		}
		
		@Override
		protected void onPostExecute(MendeleyException result) {
			appInterface.onDocumentPatched(documentId, result);
		}
	}
	
	/**
	 * Executing the api call for deleting a document in the background.
	 * sending the data to the relevant callback method in the MendeleyDocumentInterface.
	 * If the call response code is different than expected or an exception is being thrown in the process
	 * creates a new MendeleyException with the relevant information which will be passed to the application via the callback.
	 */
	protected class DeleteDocumentTask extends AsyncTask<String, Void, MendeleyException> {

		String documentId = null;
		MendeleyResponse response;
		int expectedResponse = 204;
		
		@Override
		protected MendeleyException doInBackground(String... params) {
			
			String url = params[0];
			String id = params[1];
			
			HttpsURLConnection con = null;

			InputStream is = null;
			try {
				con = getConnection(url, "DELETE");
				con.connect();
				
				response = getResponse(con);

				if (response.responseCode != expectedResponse) {
					return new HttpResponseException("Response code: " + response.responseCode);
				} else {

					is = con.getInputStream();			
					is.close();
					
					documentId = id;
					return null;
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
				if (con != null) {
					con.disconnect();
				}	
			}
		}
		
		@Override
		protected void onPostExecute(MendeleyException result) {
			appInterface.onDocumentDeleted(documentId, result);
		}
	}
	
	/**
	 * Executing the api call for posting trash document in the background.
	 * sending the data to the relevant callback method in the MendeleyDocumentInterface.
	 * If the call response code is different than expected or an exception is being thrown in the process
	 * creates a new MendeleyException with the relevant information which will be passed to the application via the callback.
	 */
	protected class PostTrashDocumentTask extends AsyncTask<String, Void, MendeleyException> {

		String documentId = null;
		MendeleyResponse response;
		int expectedResponse = 204;
		
		@Override
		protected MendeleyException doInBackground(String... params) {

			String url = params[0];
			String id = params[1];

			HttpsURLConnection con = null;

			InputStream is = null;
			try {
				con = getConnection(url, "POST");
				con.connect();
				
				response = getResponse(con);

				if (response.responseCode != expectedResponse) {
					return new HttpResponseException("Response code: " + response.responseCode);
				} else {

					is = con.getInputStream();			
					is.close();
					
					documentId = id;
					
					return null;
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
				if (con != null) {
					con.disconnect();
				}	
			}
		}
		
		@Override
		protected void onPostExecute(MendeleyException result) {
			appInterface.onDocumentTrashed(documentId, result);
		}
	}
	
	/**
	 * Executing the api call for posting a document in the background.
	 * sending the data to the relevant callback method in the MendeleyDocumentInterface.
	 * If the call response code is different than expected or an exception is being thrown in the process
	 * creates a new MendeleyException with the relevant information which will be passed to the application via the callback.
	 */
	protected class PostDocumentTask extends AsyncTask<String, Void, MendeleyException> {

		Document document;
		MendeleyResponse response;
		int expectedResponse = 201;
		
		@Override
		protected MendeleyException doInBackground(String... params) {
			
			String url = params[0];
			String jsonString = params[1];

			HttpsURLConnection con = null;

			InputStream is = null;
			OutputStream os = null;
			
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
					document = parser.parseDocument(responseString);
					
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
			appInterface.onDocumentPosted(document, result);
		}
	}
	
	/**
	 * Executing the api call for getting a document in the background.
	 * sending the data to the relevant callback method in the MendeleyDocumentInterface.
	 * If the call response code is different than expected or an exception is being thrown in the process
	 * creates a new MendeleyException with the relevant information which will be passed to the application via the callback.
	 */
	protected class GetDocumentTask extends AsyncTask<String, Void, MendeleyException> {

		MendeleyResponse response;
		Document document;
		String date;
		int expectedResponse = 200;
		
		@Override
		protected MendeleyException doInBackground(String... params) {

				String url = params[0];

				HttpsURLConnection con = null;

				InputStream is = null;
				try {
					con = getConnection(url, "GET");
					con.connect();

					response = getResponse(con);
					
					if (response.responseCode != expectedResponse) {
						return new HttpResponseException("Server response : " + response.header);
					} else {

						is = con.getInputStream();
						String jsonString = getJsonString(is);					
						is.close();
						
						JasonParser parser = new JasonParser();
						document = parser.parseDocument(jsonString);
						
						return null;
					}
					
				} catch (IOException | JSONException e) {
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
			appInterface.onDocumentReceived(document, result);
		}
	}


	/**
	 * Executing the api call for posting a document in the background.
	 * sending the data to the relevant callback method in the MendeleyDocumentInterface.
	 * If the call response code is different than expected or an exception is being thrown in the process
	 * creates a new MendeleyException with the relevant information which will be passed to the application via the callback.
	 */
	protected class GetDocumentsTask extends AsyncTask<String, Void, MendeleyException> {

		List<Document> documents;
		MendeleyResponse response = null;
		int expectedResponse = 200;

		@Override
		protected MendeleyException doInBackground(String... params) {

			String url = params[0];

			HttpsURLConnection con = null;

			InputStream is = null;
			try {
				con = getConnection(url, "GET");
				con.connect();
				
				response = getResponse(con);				

				if (response.responseCode != 200) {
					return new HttpResponseException("Response code: " + response.responseCode);
				} else {			
				
					is = con.getInputStream();
					String jsonString = getJsonString(is);					
					is.close();
			
						
					JasonParser parser = new JasonParser();
					documents = parser.parseDocumentList(jsonString);
					
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
			appInterface.onDocumentsReceived(documents, result);			
		}
	}
	
}
