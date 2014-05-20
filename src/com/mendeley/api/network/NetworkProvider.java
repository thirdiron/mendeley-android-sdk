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
import com.mendeley.api.network.interfaces.MendeleyDocumentsInterface;

public class NetworkProvider {

	public static int documentsLimit = 100;
	protected static String tokenType = null;
	protected static String accessToken = null;
	protected static String refreshToken = null;
	protected static String expiresAt = null;
	protected static int expiresIn = -1;
	
	AuthenticationInterface authInterface;
	
	NetworkProvider(AuthenticationInterface authInterface) {
		this.authInterface = authInterface;
	}

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
	
	protected HttpsURLConnection getConnection(String newUrl, String method) throws IOException {
		HttpsURLConnection con = null;
		URL url = new URL(newUrl);
		con = (HttpsURLConnection) url.openConnection();
		con.setReadTimeout(10000);
		con.setConnectTimeout(15000 );
		con.setRequestMethod(method);
		con.setDoInput(true);
		con.addRequestProperty("Authorization", "Bearer " + NetworkProvider.accessToken);
		con.addRequestProperty("Content-type", "application/vnd.mendeley-document.1+json");
		con.addRequestProperty("Accept", "application/vnd.mendeley-document.1+json");

		return con;
	}
	
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
	
	
	
	
	
	//Testing
	
	public NetworkProvider() {
		
	}
	
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
