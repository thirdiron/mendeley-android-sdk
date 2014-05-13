package com.mendeley.api.network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpParams;
import org.json.JSONException;

import android.os.AsyncTask;
import android.util.Log;

import com.mendeley.api.model.Document;
import com.mendeley.api.network.interfaces.AuthenticationInterface;
import com.mendeley.api.network.interfaces.MendeleyAPICallsInterface;

class DefaultNetworkProvider {

	public static int documentsLimit = 100;
	protected static String tokenType = null;
	protected static String accessToken = null;
	protected static String refreshToken = null;
	protected static int expiresIn = 0;
	
	AuthenticationInterface authInterface;
	MendeleyAPICallsInterface appInterface;
	
	DefaultNetworkProvider(AuthenticationInterface authInterface, MendeleyAPICallsInterface appInterface) {
		this.authInterface = authInterface;
		this.appInterface = appInterface;
	}
	
//	private void removeFromDB(String id) {
//		int rows = MendeleyNetworkProvider.dbHelper.delete(id);
//		
//		Log.e("", "db  delete rows: " + rows);
//	}
//	
//	private String insertDB(String url) {
//		
//		long id = MendeleyNetworkProvider.dbHelper.insert(url);
//		
//		Log.e("", "db  insert id: " + id);
//		
//		return id+"";
//
//	}
	
	protected void doDeleteDocument(String url, String id) throws ClientProtocolException, IOException {
		 new DeleteDocumentTask().execute(url + id, id);
	}
	
	protected void doPostDocument(String url, Document document) throws ClientProtocolException, IOException {

		JasonParser parser = new JasonParser();
		try {
			new PostDocumentTask().execute(url, parser.jsonFromDocument(document));			
		} catch (JSONException e) {
			Log.e("", "", e);
		}
	}
	
	protected void doPostTrashDocument(String url, String id) throws ClientProtocolException, IOException {
		 new PostTrashDocumentTask().execute(url + id + "/trash", id);
	}
	
	protected void doGetDocument(String url, String id) throws ClientProtocolException, IOException {
		 new GetDocumentTask().execute(url + id);
	}
	
	protected void doGetDocuments(String url) throws ClientProtocolException, IOException {
		
//		String id = insertDB(url);		
		new GetDocumentsTask().execute(url + "?limit=" + documentsLimit);		  
	}
	
	private String formatDate(Date date) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        return df.format(date);
	}
	
	protected void doPatchDocument(String url, String id, Date date, Document document) throws ClientProtocolException, IOException {

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
	
	protected class HttpPatch extends HttpEntityEnclosingRequestBase {

	    public final static String METHOD_NAME = "PATCH";

	    public HttpPatch() {
	        super();
	    }

	    public HttpPatch(final URI uri) {
	        super();
	        setURI(uri);
	    }

	    public HttpPatch(final String uri) {
	        super();
	        setURI(URI.create(uri));
	    }

	    @Override
	    public String getMethod() {
	        return METHOD_NAME;
	    }

	}
	
	protected class PatchDocumentTask extends AsyncTask<String, Void, String> {

		String documentId = null;
		
		@Override
		protected String doInBackground(String... params) {
			
			String url = params[0];
			String id = params[1];
			String date = params[2];
			String jsonString = params[3];

			HttpClient httpclient = new DefaultHttpClient();
			HttpPatch httpPatch = new HttpPatch(url);
			httpPatch.setHeader("Authorization", "Bearer " + DefaultNetworkProvider.accessToken);
			httpPatch.setHeader("Content-type", "application/vnd.mendeley-document.1+json");
			httpPatch.setHeader("Accept", "application/json");
			
			Log.e("", "date: "+date);
			
			if (date != null) {
				httpPatch.setHeader("If-Unmodified-Since", date);
			}

	        try {
	        	
	        	httpPatch.setEntity(new StringEntity(jsonString));

	        	HttpResponse response = httpclient.execute(httpPatch);				
				int responseCode = response.getStatusLine().getStatusCode();	        	
				
				if (responseCode != 204) {
					return "callError";
				} else {
					
					documentId = id;
					return "ok";
				}
			} catch (IOException e) {
				Log.e("", "", e);
				return null;
			}
		}
		
		@Override
		protected void onPostExecute(String result) {
			
			if (result == null) {
				authInterface.onAPICallFail();
			} else {
				if (result.equals("callError")) {
					authInterface.onAuthenticationFail();
				}
				else {
					appInterface.onDocumentPatched(documentId);
				}
			}
		}
		
	}
	
	protected class DeleteDocumentTask extends AsyncTask<String, Void, String> {

		String documentId = null;
		
		@Override
		protected String doInBackground(String... params) {
			
			String url = params[0];
			String id = params[1];

			HttpsURLConnection con = null;

			InputStream is = null;
			try {
				con = getConnection(url, "DELETE");
				con.connect();
				
				int responseCode = con.getResponseCode();

				if (responseCode != 204) {
					return "callError";
				} else {

					is = con.getInputStream();			
					is.close();
					
					documentId = id;
					return "ok";
				}
				
			}	catch (IOException e) {
				Log.e("", "", e);
				return null;
			} finally {
				if (is != null) {
					try {
						is.close();
						is = null;
					} catch (IOException e) {
						Log.e("", "", e);
					}
				}
			}
		}
		
		@Override
		protected void onPostExecute(String result) {
			
			if (result == null) {
				authInterface.onAPICallFail();
			} else {
				if (result.equals("callError")) {
					authInterface.onAuthenticationFail();
				}
				else {
					appInterface.onDocumentDeleted(documentId);
				}
			}
		}
		
	}
	
	protected class PostTrashDocumentTask extends AsyncTask<String, Void, String> {

		String documentId = null;
		
		@Override
		protected String doInBackground(String... params) {

			String url = params[0];
			String id = params[1];

			HttpsURLConnection con = null;

			InputStream is = null;
			try {
				con = getConnection(url, "POST");
				con.connect();
				
				int responseCode = con.getResponseCode();

				if (responseCode != 204) {
					return "callError";
				} else {

					is = con.getInputStream();			
					is.close();
					
					documentId = id;
					return "ok";
				}
				
			}	catch (IOException e) {
				e.printStackTrace();
				return null;
			} finally {
				if (is != null) {
					try {
						is.close();
						is = null;
					} catch (IOException e) {
						Log.e("", "", e);
					}
				}
			}
		}
		
		@Override
		protected void onPostExecute(String result) {
			
			if (result == null) {
				authInterface.onAPICallFail();
			} else {
				if (result.equals("callError")) {
					authInterface.onAuthenticationFail();
				}
				else {
					appInterface.onDocumentTrashed(documentId);
				}
			}
		}
	}
	
	protected class PostDocumentTask extends AsyncTask<String, Void, String> {

		Document document;
		
		@Override
		protected String doInBackground(String... params) {
			
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
				
				int responseCode = con.getResponseCode();

				if (responseCode != 201) {
					return "callError";
				} else {

					is = con.getInputStream();
					String responseString = getJsonString(is);					
					is.close();
					
					JasonParser parser = new JasonParser();
					document = parser.parseDocument(responseString);
					
					return "ok";
				}
				
			}	catch (IOException | JSONException e) {
				e.printStackTrace();
				return null;
			} finally {
				if (is != null) {
					try {
						is.close();
						is = null;
					} catch (IOException e) {
						Log.e("", "", e);
					}
				}
				if (os != null) {
					try {
						os.close();
						os = null;
					} catch (IOException e) {
						Log.e("", "", e);
					}
				}
			}
		}
		
		@Override
		protected void onPostExecute(String result) {
			
			if (result == null) {
				authInterface.onAPICallFail();
			} else {
				if (result.equals("callError")) {
					authInterface.onAuthenticationFail();
				}
				else {
					appInterface.onDocumentPosted(document);
				}
			}
		}
		
	}
	
	protected class GetDocumentTask extends AsyncTask<String, Void, String> {

		HttpResponse response = null;
		Document document;
		String date;
		
		@Override
		protected String doInBackground(String... params) {

				String url = params[0];

				HttpsURLConnection con = null;

				InputStream is = null;
				try {
					con = getConnection(url, "GET");
					con.connect();
		
					int responseCode = con.getResponseCode();

					if (responseCode != 200) {
						return "callError";
					} else {

						is = con.getInputStream();
						String jsonString = getJsonString(is);					
						is.close();
						
						JasonParser parser = new JasonParser();
						document = parser.parseDocument(jsonString);
						
						return "ok";
					}
					
				}	catch (IOException | JSONException e) {
					e.printStackTrace();
					return null;
				} finally {
					if (is != null) {
						try {
							is.close();
							is = null;
						} catch (IOException e) {
							Log.e("", "", e);
						}
					}
				}
		}
		
		@Override
		protected void onPostExecute(String result) {
			
			if (result == null) {
				authInterface.onAPICallFail();
			} else {
				if (result.equals("callError")) {
					authInterface.onAuthenticationFail();
				}
				else {
					appInterface.onDocumentReceived(document);
					
				}
			}
		}
	}
	
	protected class GetDocumentsTask extends AsyncTask<String, Void, String> {

		HttpResponse response = null;
		List<Document> documents;
		String link;
		String date;
//		String dbId;
		
		@Override
		protected String doInBackground(String... params) {
			
			String url = params[0];
//			dbId = params[1];

//			HttpClient httpclient = HttpClientFactory.getThreadSafeClient();
//			
//			HttpGet httpGet = new HttpGet(url_);
//			httpGet.setHeader("Authorization", "Bearer " + DefaultNetworkProvider.accessToken);
//			
			
			HttpsURLConnection con = null;

			InputStream is = null;
			try {
				con = getConnection(url, "GET");
				con.connect();
				
				Map<String, List<String>> headersMap = con.getHeaderFields();
	
				int responseCode = con.getResponseCode();

				if (responseCode != 200) {
					return "callError";
				} else {

				    date = headersMap.get("Date").get(0);					
					link = headersMap.get("Link").get(0);
					
					link = link.substring(link.indexOf("<")+1, link.indexOf(">"));					
					Log.e("", "link: " + link);					
//					doGetDocuments(link);
				
					is = con.getInputStream();
					String jsonString = getJsonString(is);					
					is.close();
					
					JasonParser parser = new JasonParser();
					documents = parser.parseDocumentList(jsonString);
					
					return "ok";
				}
				
			}	catch (IOException | JSONException e) {
				e.printStackTrace();
				return null;
			} finally {
				if (is != null) {
					try {
						is.close();
						is = null;
					} catch (IOException e) {
						Log.e("", "", e);
					}
				}
			}
		}
		
		@Override
		protected void onPostExecute(String result) {
			
			if (result == null) {
				authInterface.onAPICallFail();
			} else {
				if (result.equals("callError")) {
					authInterface.onAuthenticationFail();
				}
				else {
//					if (dbId != null) {
//						removeFromDB(dbId);
//					}					
					appInterface.onDocumentsReceived(documents);					
				}
			}
		}
	}
	
	
	private HttpsURLConnection getConnection(String newUrl, String method) throws IOException {
		HttpsURLConnection con = null;
		URL url = new URL(newUrl);
		con = (HttpsURLConnection) url.openConnection();
		con.setReadTimeout(10000);
		con.setConnectTimeout(15000 );
		con.setRequestMethod(method);
		con.setDoInput(true);
		con.addRequestProperty("Authorization", "Bearer " + DefaultNetworkProvider.accessToken);
		con.addRequestProperty("Content-type", "application/vnd.mendeley-document.1+json");
		con.addRequestProperty("Accept", "application/vnd.mendeley-document.1+json");
		
		return con;
	}
	
	protected String getJsonString(InputStream stream) throws IOException {

		StringBuffer data = new StringBuffer();
		InputStreamReader isReader = null;
		BufferedReader br = null;
		
		try {
			
			isReader = new InputStreamReader(stream); 
            br = new BufferedReader(isReader);
            String brl = ""; 
            while ((brl = br.readLine()) != null) {
        	    data.append(brl);
            }
            
		} finally {
			stream.close();
            isReader.close();
            br.close();
		}
		
		return data.toString();
	}
	
	public static class HttpClientFactory {

	    private static DefaultHttpClient client;

	    public synchronized static DefaultHttpClient getThreadSafeClient() {
	  
	        if (client != null)
	            return client;
	         
	        client = new DefaultHttpClient();
	        
	        ClientConnectionManager mgr = client.getConnectionManager();
	        
	        HttpParams params = client.getParams();
	        client = new DefaultHttpClient(
	        new ThreadSafeClientConnManager(params,
	            mgr.getSchemeRegistry()), params);
	  
	        return client;
	    } 
	}

}
