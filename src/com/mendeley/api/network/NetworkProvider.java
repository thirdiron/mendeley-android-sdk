package com.mendeley.api.network;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
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

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.HttpParams;
import org.json.JSONException;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import com.mendeley.api.exceptions.HttpResponseException;
import com.mendeley.api.exceptions.JsonParsingException;
import com.mendeley.api.exceptions.MendeleyException;
import com.mendeley.api.model.Folder;
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
					case "Location":
						response.location = headersMap.get(key).get(0);	
						break;
				}
			} else {
				response.header = headersMap.get(key).get(0);	
			}
		}		
	}
	
	/**
	 * Creates an error message string from a given URLConnection object,
	 * which includes the response code, response message and the error stream from the server
	 * 
	 * @param con the URLConnection object
	 * @return the error message string
	 */
	protected String getErrorMessage(HttpsURLConnection con) {
		String message = "";
		InputStream is = null;
		try {
			message = con.getResponseCode() + " "  + con.getResponseMessage();
			is = con.getErrorStream();
			String responseString = "";
			if (is != null) {
				responseString = getJsonString(is);
			}
			message += "\n" + responseString;
		} catch (IOException e) {
		} finally {
			if (is != null) {
				try {
					is.close();
					is = null;
				} catch (IOException e) {
				}
			}
		}
		return message;
	}
	
	/**
	 * AsyncTask class is extended by all AsyncTasks in the NetworkProvider subclasses
	 * The class holds MendeleyResponse and stream objects that should be used in the subclasses.
	 */
	protected abstract class NetworkTask extends AsyncTask<String, Void, MendeleyException> {

		MendeleyResponse response = new MendeleyResponse();
		int expectedResponse;
		InputStream is = null;
		OutputStream os = null;
		HttpsURLConnection con = null;
		
		protected void closeConnection() {
			if (con != null) {
				con.disconnect();
			}	
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
			if (os != null) {
				try {
					os.close();
				} catch (IOException e) {
				}
			}
		}
		
		@Override
		protected void onPostExecute(MendeleyException result) {	
			response.mendeleyException = result;
		}
	}
	
	/**
	 * Creates an error message string from a given HttpResponse object,
	 * which includes the response code, response message and the error stream from the server
	 * 
	 * @param con the URLConnection object
	 * @return the error message string
	 */
	protected String getErrorMessage(HttpResponse response) {
		String message = "";
		InputStream is = null;
		try {
			message = response.getStatusLine().getStatusCode() + " "  + response.getStatusLine().getReasonPhrase();
			is = response.getEntity().getContent();
			String responseString = "";
			if (is != null) {
				responseString = getJsonString(is);
			}
			message += "\n" + responseString;
		} catch (IOException e) {
		} finally {
			if (is != null) {
				try {
					is.close();
					is = null;
				} catch (IOException e) {
				}
			}
		}
		return message;
	}
	
	/**
	 * Creating HttpPatch object with the given url, the date string
	 * if not null and mendeley document content type.
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

	/**
	 * Creating HttpPatch object with the given url 
	 * and mendeley folder update content type.
	 * 
	 * @param url the call url
	 * @return the HttpPatch object
	 */
	protected HttpPatch getFolderHttpPatch(String url) {
		HttpPatch httpPatch = new HttpPatch(url);
		httpPatch.setHeader("Authorization", "Bearer " + NetworkProvider.accessToken);
		httpPatch.setHeader("Content-type", "application/vnd.mendeley-folder-update-folder.1+json");

		return httpPatch;
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
		
		con.addRequestProperty("Authorization", "Bearer " + NetworkProvider.accessToken);

		return con;
	}
	
	/**
	 * Creating HttpsURLConnection object with the given url and request method
	 * without the authorization header. This is used for downloading a file from the server.
	 * 
	 * @param url the call url
	 * @param method the required request method
	 * @return the HttpsURLConnection object
	 * @throws IOException
	 */
	protected HttpsURLConnection getDownloadConnection(String url, String method) throws IOException {
		HttpsURLConnection con = null;
		URL callUrl = new URL(url);
		con = (HttpsURLConnection) callUrl.openConnection();
		con.setReadTimeout(10000);
		con.setConnectTimeout(15000);
		con.setRequestMethod(method);

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
