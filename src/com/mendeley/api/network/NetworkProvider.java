package com.mendeley.api.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

import android.annotation.SuppressLint;
import android.os.AsyncTask;

import com.mendeley.api.exceptions.MendeleyException;
import com.mendeley.api.params.Page;

/**
 * This class provides common functionality for the other network providers that subclass it.
 *
 */
public class NetworkProvider {
	protected static final String apiUrl = "https://api.mendeley.com/";
	
	public static String accessToken = null;

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
	protected abstract class NetworkTask extends AsyncTask<String, Integer, MendeleyException> {
		Page next = new Page();
        String location;

		InputStream is = null;
		OutputStream os = null;
		HttpsURLConnection con = null;
		
		protected abstract int getExpectedResponse();

        /**
         * Extracts the headers from the given HttpsURLConnection object.
         */
        protected void getResponseHeaders() throws IOException {
            Map<String, List<String>> headersMap = con.getHeaderFields();
            for (String key : headersMap.keySet()) {
                if (key != null) {
                    switch (key) {
                        case "Date":
                        case "Vary":
                        case "Content-Type":
                        case "X-Mendeley-Trace-Id":
                        case "Connection":
                        case "Content-Length":
                        case "Content-Encoding":
                        case "Mendeley-Count":
                            // Unused
                            break;
                        case "Location":
                            location = headersMap.get(key).get(0);
                        case "Link":
                            List<String> links = headersMap.get(key);
                            String linkString = null;
                            for (String link : links) {
                                try {
                                    linkString = link.substring(link.indexOf("<")+1, link.indexOf(">"));
                                } catch (IndexOutOfBoundsException e) {}
                                if (link.indexOf("next") != -1) {
                                    next.link = linkString;
                                }
                                // "last" and "prev" links are not used
                            }
                            break;
                    }
                }
            }
        }

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
		protected void onPostExecute(MendeleyException exception) {	
			if (exception == null) {
				onSuccess();
			} else {
				onFailure(exception);
			}
		}

		protected void onProgressUpdate(Integer[] progress) {
			super.onProgressUpdate();
		}
		
		protected abstract void onSuccess();
		
		protected abstract void onFailure(MendeleyException exception);
	}
	
	/**
	 * Creates an error message string from a given HttpResponse object,
	 * which includes the response code, response message and the error stream from the server
	 * 
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
		con.setReadTimeout(3000);
		con.setConnectTimeout(3000);
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
		con.setReadTimeout(3000);
		con.setConnectTimeout(3000);
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
	
}
