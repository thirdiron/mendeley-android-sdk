package com.mendeley.api.network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpParams;
import org.json.JSONException;

import android.os.AsyncTask;
import android.util.Log;

import com.mendeley.api.exceptions.HttpResponseException;
import com.mendeley.api.exceptions.JsonParsingException;
import com.mendeley.api.exceptions.MendeleyException;
import com.mendeley.api.model.Document;
import com.mendeley.api.network.components.MendeleyResponse;
import com.mendeley.api.network.interfaces.AuthenticationInterface;
import com.mendeley.api.network.interfaces.MendeleyDocumentInterface;

/**
 * This class provides common functionality for the other network providers that subclass it.
 * 
 * @author Elad
 *
 */
public class NetworkProvider {

	protected static String apiUrl = "https://mix.mendeley.com:443/";
	
	public static int documentsLimit = 100;
	protected static String tokenType = null;
	protected static String accessToken = null;
	protected static String refreshToken = null;
	protected static String expiresAt = null;
	protected static int expiresIn = -1;

	/**
	 * Inner class that extends HttpEntityEnclosingRequestBase to provide PATCH request method.
	 *
	 */
	public static class HttpPatch extends HttpEntityEnclosingRequestBase {

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
	
	/**
	 * Extracting the headers from the given HttpsURLConnection object.
	 * 
	 * @param con
	 * @return MemdeleyResponse object holding the the headers data
	 * @throws IOException
	 */
	protected MendeleyResponse getResponse(HttpsURLConnection con) throws IOException {
		
		int responseCode = con.getResponseCode();
		Map<String, List<String>> headersMap = con.getHeaderFields();
		
		MendeleyResponse response = new MendeleyResponse(responseCode);
		
		for (String key : headersMap.keySet()) {

			if (key != null) {
				switch (key) {
					case "Date":
						response.date = headersMap.get(key).get(0);	
						break;
					case "Vary":
						response.vary = headersMap.get(key).get(0);	
						break;
					case "Content-Type":
						response.contentType = headersMap.get(key).get(0);	
						break;
					case "X-Mendeley-Trace-Id":
						response.traceId = headersMap.get(key).get(0);	
						break;
					case "Connection":
						response.connection = headersMap.get(key).get(0);	
						break;
					case "Link":
						response.link = headersMap.get(key).get(0);	
						break;
					case "Content-Length":
						response.contentLength = headersMap.get(key).get(0);	
						break;
					case "Content-Encoding":
						response.contentEncoding = headersMap.get(key).get(0);	
						break;
				}
			} else {
				response.header = headersMap.get(key).get(0);	
			}
		}

		return response;
		
	}
	
	/**
	 * Creating HttpPatch object with the given url and and date string.
	 * Also adding the access token and other required headers.
	 * 
	 * @param url the call url
	 * @param date the required date string
	 * @return the HttpPatch object
	 */
	protected HttpPatch getHttpPatch(String url, String date) {
		HttpPatch httpPatch = new HttpPatch(url);
		httpPatch.setHeader("Authorization", "Bearer " + NetworkProvider.accessToken);
		httpPatch.setHeader("Content-type", "application/vnd.mendeley-document.1+json");
		httpPatch.setHeader("Accept", "application/json");
		if (date != null) {
			httpPatch.setHeader("If-Unmodified-Since", date);
		}
		
		return httpPatch;
	}
	
	protected HttpPatch getHttpPatch(String url) {
		HttpPatch httpPatch = new HttpPatch(url);
		httpPatch.setHeader("Authorization", "Bearer " + NetworkProvider.accessToken);
		httpPatch.setHeader("Content-type", "application/vnd.mendeley-document.1+json");
		httpPatch.setHeader("Accept", "application/json");		
		return httpPatch;
	}
	
	
	protected HttpGet getHttpGet(String url) {
		HttpGet httpGet = new HttpGet(url);
		httpGet.setHeader("Authorization", "Bearer " + NetworkProvider.accessToken);
		httpGet.setHeader("Content-type", "application/vnd.mendeley-document.1+json");
//		httpGet.setHeader("Accept", "application/vnd.mendeley-document.1+json");
		
		return httpGet;
	}
	
	/**
	 * Creating HttpsURLConnection object with the given url and request method.
	 * Also adding the access token and other required request properties.
	 * 
	 * @param url the call url
	 * @param method the required request method
	 * @return the HttpsURLConnection object
	 * @throws IOException
	 */
	protected HttpsURLConnection getConnection(String url, String method) throws IOException {
		HttpsURLConnection con = null;
		URL callUrl = new URL(url);
		con = (HttpsURLConnection) callUrl.openConnection();
		con.setReadTimeout(10000);
		con.setConnectTimeout(15000);
		con.setRequestMethod(method);
		con.setDoInput(true);
//		con.setDoOutput(true);
		con.addRequestProperty("Authorization", "Bearer " + NetworkProvider.accessToken);
		con.addRequestProperty("Content-type", "application/vnd.mendeley-document.1+json"); 

		
		
//		con.addRequestProperty("Accept", "application/vnd.mendeley-document.1+json");

		return con;
	}
	
	//TODO: fix method
	protected HttpsURLConnection getFileConnection(String newUrl, String method) throws IOException {
		HttpsURLConnection con = null;
		URL url = new URL(newUrl);
		con = (HttpsURLConnection) url.openConnection();
		con.setReadTimeout(10000);
		con.setConnectTimeout(15000 );
		con.setRequestMethod(method);
		con.setDoInput(true);
		con.addRequestProperty("Authorization", "Bearer " + NetworkProvider.accessToken);

		
//		con.addRequestProperty("Vary", "Accept-Encoding, User-Agent");
//		con.addRequestProperty("Accept-Encoding", "gzip,deflate,sdch");
//		con.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
//		con.addRequestProperty("Connection", "keep-alive");
//		con.addRequestProperty("Cache-Control", "must-revalidate,no-cache,no-store");
//

		return con;
	}
	
	//TODO: fix method
	private HttpsURLConnection getPatchConnection(String newUrl) throws IOException {
		HttpsURLConnection con = null;
		URL url = new URL(newUrl);
		con = (HttpsURLConnection) url.openConnection();
		con.setReadTimeout(10000);
		con.setRequestMethod("POST");
		con.setConnectTimeout(15000 );
		con.setDoInput(true);
		con.addRequestProperty("Authorization", "Bearer " + NetworkProvider.accessToken);
		con.addRequestProperty("Content-type", "application/vnd.mendeley-document.1+json");
		con.addRequestProperty("Accept", "application/vnd.mendeley-document.1+json");
		con.addRequestProperty("x-method-override", "PATCH");
		
		
		//PATCH /documents/46f5e4a8-d333-34aa-a028-4deea5064395 HTTP/1.1

				
		Map<String, List<String>> h = con.getRequestProperties();

		
		for (String key : h.keySet()) {
			Log.e("request", " R -> "+key+":"+h.get(key).get(0));
		}
		
		return con;
	}


	/**
	 * Extracting json String from the given InputStream object.
	 * 
	 * @param stream the InputStream holding the json string
	 * @return the json string
	 * @throws IOException
	 */
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
	
	//TODO: not used for now
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
	
	
	
	
	/**
	 * public default constructor for testing.
	 */
	public NetworkProvider() {
		
	}
	
	/**
	 * Creating and invoking a method from the given parameters.
	 * Used for testing private methods.
	 * 
	 * @param methodName the method to invoke
	 * @param args the method's arguments
	 * @return the result of the invoked method
	 * @throws NoSuchMethodException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public Object getMethodToTest(String methodName, ArrayList<Object> args) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		Object result = null;
		
		Object[] values = new Object[args.size()];
		Class[] classes = new Class[args.size()];
		
		for (int i = 0; i < args.size(); i++) {
			if (args.get(i).getClass().getName().equals("sun.net.www.protocol.https.HttpsURLConnectionImpl")) {
				classes[i] =  javax.net.ssl.HttpsURLConnection.class;
			} else if (args.get(i).getClass().getName().equals("sun.net.www.protocol.http.HttpURLConnection$HttpInputStream") ||
				       args.get(i).getClass().getName().equals("org.apache.http.conn.EofSensorInputStream")) { 
				classes[i] =  java.io.InputStream.class;
			} else {
				classes[i] = args.get(i).getClass();
			}
			
			values[i] = args.get(i);
		}

		Method method = null;
		
		if (args.size() > 0) {
			method = this.getClass().getDeclaredMethod(methodName, classes);	
			method.setAccessible(true);
			result = method.invoke(this, values);
		} else {
			method = this.getClass().getDeclaredMethod(methodName);
			method.setAccessible(true);
			result = method.invoke(this);
		}

		return result;
	}


}
