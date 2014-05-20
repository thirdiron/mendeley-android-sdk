package com.mendeley.api.network;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import com.mendeley.api.network.components.MendeleyResponse;
import com.mendeley.api.network.interfaces.AuthenticationInterface;
import com.mendeley.api.network.interfaces.MendeleyDocumentsInterface;

public class DocumentsNetworkProvider extends NetworkProvider {

	public static int documentsLimit = 100;
	protected static String tokenType = null;
	protected static String accessToken = null;
	protected static String refreshToken = null;
	protected static int expiresIn = 0;
	
	MendeleyDocumentsInterface appInterface;
	
	DocumentsNetworkProvider(AuthenticationInterface authInterface, MendeleyDocumentsInterface docInterface) {
		super(authInterface);
		this.appInterface = docInterface;
	}
	
	protected void doDeleteDocument(String url, String id) throws IOException {
		 new DeleteDocumentTask().execute(url + id, id);
	}
	
	protected void doPostDocument(String url, Document document) throws IOException {

		JasonParser parser = new JasonParser();
		try {
			new PostDocumentTask().execute(url, parser.jsonFromDocument(document));			
		} catch (JSONException e) {
			Log.e("", "", e);
		}
	}
	
	protected void doPostTrashDocument(String url, String id) throws IOException {
		 new PostTrashDocumentTask().execute(url + id + "/trash", id);
	}
	
	protected void doGetDocument(String url, String id) throws IOException {
		 new GetDocumentTask().execute(url + id);
	}
	
	protected void doGetDocuments(String url) throws IOException {
		new GetDocumentsTask().execute(url + "?limit=" + documentsLimit);		  
	}
	
	private String formatDate(Date date) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        return df.format(date);
	}
	
	protected void doPatchDocument(String url, String id, Date date, Document document) throws IOException {

		String dateString = null;
		
		if (date != null) {
			dateString = formatDate(date);
		}
		
		JasonParser parser = new JasonParser();
		try {
			document.title += " patched!";
			new PatchDocumentTask().execute(url+id, id, dateString, parser.jsonFromDocument(document));
			
		} catch (JSONException e) {
			Log.e("", "", e);
		}
	}
	
	protected class PatchDocumentTask extends AsyncTask<String, Void, MendeleyException> {

		MendeleyResponse response;
		String documentId = null;
		
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
				
				if (responseCode != 204) {
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
	
	protected class DeleteDocumentTask extends AsyncTask<String, Void, MendeleyException> {

		String documentId = null;
		MendeleyResponse response;
		
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

				if (response.responseCode != 204) {
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
			}
		}
		
		@Override
		protected void onPostExecute(MendeleyException result) {
			appInterface.onDocumentDeleted(documentId, result);
		}
	}
	
	protected class PostTrashDocumentTask extends AsyncTask<String, Void, MendeleyException> {

		String documentId = null;
		MendeleyResponse response;
		
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

				if (response.responseCode != 204) {
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
			}
		}
		
		@Override
		protected void onPostExecute(MendeleyException result) {
			appInterface.onDocumentTrashed(documentId, result);
		}
	}
	
	protected class PostDocumentTask extends AsyncTask<String, Void, MendeleyException> {

		Document document;
		MendeleyResponse response;
		
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

				if (response.responseCode != 201) {
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
			}
		}
		
		@Override
		protected void onPostExecute(MendeleyException result) {
			appInterface.onDocumentPosted(document, result);
		}
	}
	
	protected class GetDocumentTask extends AsyncTask<String, Void, MendeleyException> {

		MendeleyResponse response;
		Document document;
		String date;
		
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
				}
		}
		
		@Override
		protected void onPostExecute(MendeleyException result) {
			appInterface.onDocumentReceived(document, result);
		}
	}

	protected class GetDocumentsTask extends AsyncTask<String, Void, MendeleyException> {

		List<Document> documents;
		MendeleyResponse response = null;

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
			}
		}
		
		@Override
		protected void onPostExecute(MendeleyException result) {			
			appInterface.onDocumentsReceived(documents, result);			
		}
	}

}
