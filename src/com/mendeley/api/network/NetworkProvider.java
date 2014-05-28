package com.mendeley.api.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpParams;

import android.annotation.SuppressLint;
import android.util.Log;

import com.mendeley.api.network.components.MendeleyResponse;

/**
 * This class provides common functionality for the other network providers that subclass it.
 *
 */
public class NetworkProvider {

	protected static String apiUrl = "https://mix.mendeley.com:443/";
	
	protected static String tokenType = null;
	protected static String accessToken = null;
	protected static String refreshToken = null;
	protected static String expiresAt = null;
	protected static int expiresIn = -1;
	
	@SuppressLint("SimpleDateFormat")
	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

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
	 * @param con the connection object to get headers from
	 * @param response the response object to hold header data
	 * @throws IOException
	 */
	protected void getResponseHeaders(Map<String, List<String>> headersMap, MendeleyResponse response) throws IOException {

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
						List<String> links = headersMap.get(key);
						String linkString = null;
						for (String link : links) {
							try {
								linkString = link.substring(link.indexOf("<")+1, link.indexOf(">"));
							} catch (IndexOutOfBoundsException e) {}
							if (link.indexOf("next") != -1) {
								response.linkNext = linkString;
							}
							if (link.indexOf("last") != -1) {
								response.linkLast = linkString;
							}
						}
						break;
					case "Mendeley-Count":
						response.mendeleyCount = Integer.parseInt(headersMap.get(key).get(0));
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
	
	protected HttpPatch getHttpPatchDocument(String url, String date) {
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
	
	protected HttpPatch getFolderHttpPatch(String url) {
		HttpPatch httpPatch = new HttpPatch(url);
		httpPatch.setHeader("Authorization", "Bearer " + NetworkProvider.accessToken);
		httpPatch.setHeader("Content-type", "application/vnd.mendeley-folder-update-folder.1+json");

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
		con.addRequestProperty("Authorization", "Bearer " + NetworkProvider.accessToken);

		return con;
	}
	
	protected HttpsURLConnection getFileConnection(String url, String method) throws IOException {
		HttpsURLConnection con = null;
		URL callUrl = new URL(url);
		con = (HttpsURLConnection) callUrl.openConnection();
		con.setReadTimeout(10000);
		con.setConnectTimeout(15000);
		con.setRequestMethod(method);
		con.addRequestProperty("Authorization", "Bearer " + NetworkProvider.accessToken);

		return con;
	}
	
	//TODO: fix method
	protected HttpsURLConnection getPatchConnection(String newUrl) throws IOException, NoSuchFieldException, IllegalAccessException, IllegalArgumentException {

		HttpsURLConnection con = null;
		URL url = new URL(newUrl);
		con = (HttpsURLConnection) url.openConnection();
		
		final Class<?> httpURLConnectionClass = con.getClass();
        final Class<?> parentClass = httpURLConnectionClass.getSuperclass();
        final Field methodField;
		methodField = parentClass.getSuperclass().getDeclaredField("method");
		methodField.setAccessible(true);
        methodField.set(con, "PATCH");
        

		con.setReadTimeout(10000);
		con.setConnectTimeout(15000 );
		con.setDoInput(true);

		con.addRequestProperty("Authorization", "Bearer " + NetworkProvider.accessToken);
		con.addRequestProperty("Content-type", "application/vnd.mendeley-document.1+json");


		
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
	
	
	protected String getApiUrl() {
		return apiUrl;
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
	public Object getResultFromMethod(String methodName, ArrayList<Object> args) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		
		Object result = null;
		
		Object[] values = new Object[args.size()];
		@SuppressWarnings("rawtypes")
		Class[] classes = new Class[args.size()];
		
		for (int i = 0; i < args.size(); i++) {
			if (args.get(i).getClass().getName().equals("sun.net.www.protocol.https.HttpsURLConnectionImpl")) {
				classes[i] =  javax.net.ssl.HttpsURLConnection.class;
			} else if (args.get(i).getClass().getName().equals("sun.net.www.protocol.http.HttpURLConnection$HttpInputStream") ||
				       args.get(i).getClass().getName().equals("org.apache.http.conn.EofSensorInputStream")) { 
				classes[i] =  java.io.InputStream.class;
			} else if (args.get(i).getClass().equals(HashMap.class)) { 
				classes[i] =  Map.class;
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
