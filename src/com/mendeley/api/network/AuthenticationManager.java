package com.mendeley.api.network;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.mendeley.api.network.interfaces.AuthenticationInterface;

/**
 * This class is responsible for authenticating the user, 
 * using a WebView for displaying the authentication web page.  
 *
 */
public class AuthenticationManager implements Serializable {


	private static AuthenticationManager manager;
	private static final long serialVersionUID = 1L;
	String authorizationCode;	
	Handler refreshHandler;	
	JsonParser jsonParser = new JsonParser();
	
	final static String TOKENS_URL = "https://api-oauth2.mendeley.com/oauth/token";
	final static String OUATH2_URL = "https://api-oauth2.mendeley.com/oauth/authorize";
	final static String GRANT_TYPE_AUTH = "authorization_code";
	final static String GRANT_TYPE_REFRESH = "refresh_token";
	final static String REDIRECT_URI = "http://localhost/auth_return";
	final static String SCOPE = "all";
	final static String RESPONSE_TYPE = "code";

	Context context;
	CredentialsManager credentialsManager;	
	AuthenticationInterface authInterface;
	
	/**
	 *  The constructor takes context which will be used for displaying the WebView
	 *  and an instance of AuthenticationInterface which will be used for callbacks, 
	 *  once user has been authentication, or authentication has failed.
	 *  
	 * @param context the context object
	 * @param authInterface the AuthenticationInterface instance for callbacks
	 */
	protected AuthenticationManager (Context context, AuthenticationInterface authInterface) {
		this.context = context;
		this.authInterface = authInterface;
		credentialsManager = new CredentialsManager(context);
	}
	
	protected static AuthenticationManager getInstance() {
		return manager;
	}
	
	/**
	 * Querying the CredentialManager if credentials are already stored on the device.
	 * 
	 * @return true if credentials exists, false otherwise.
	 */
	protected boolean hasCredentials() {
		return credentialsManager.hasCredentials();
	}
	
	/**
	 * Forwarding a call to the AuthenticationManager to clear user credentials from the device.
	 */
	protected void clearCredentials() {
		credentialsManager.clearCredentials();
	}
	
	/**
	 * Authenticating the User. 
	 * If credentials exist already, creating a RefreshHandler for refreshing the access token,
	 * otherwise displaying a WebView with the authentication web page, in which the user will have to
	 * enter his username and password.
	 */
	public void authenticate() {
		if (hasCredentials()) {
			createRefreshHandler(true);
		} else {
			startSignInActivity();		
		}
	}
	
	/**
	 * Starting the sign in activity.
	 */
	private void startSignInActivity() {
		manager = this;
		Intent intent = new Intent(context, SignInActivity.class);

		((Activity) context).startActivity(intent);	
	}
	  
	/**
	 * Creating a RefreshHandler which will refresh the access token before it expires.
	 * If immediateExecution is true, the delay of the execution will be set to 0 and the refresh
	 * will execute immediately (called from authenticate() if credentials exist already), else 
	 * will set the execution to happen before the access token is expired.
	 * 
	 * @param immediateExecution indicates whether the refresh of access token should happen immediately or not.
	 */
	private void createRefreshHandler(final boolean immediateExecution) {

		Runnable runnableNotify = new Runnable() {	
			@Override
			public void run() {
				refreshToken(immediateExecution);
			}
		};
		
		long delayMillis = immediateExecution ? 0 : (long)((NetworkProvider.expiresIn * 0.9) * 1000);
		refreshHandler = new Handler();
		
		refreshHandler.postDelayed(runnableNotify, delayMillis);
	}
	
	
	/**
	 * Convenience method to start the RefreshTokenTask
	 * 
	 * @param immediateExecution
	 */
	public void refreshToken(boolean immediateExecution) {
		new RefreshTokenTask().execute(immediateExecution);
	}
		        
    /**
     * Extracting the token details from the token string and sending them to the CredentialManager.
     * 
     * @param tokenString
     * @throws JSONException
     */
	private void getTokenDetails(String tokenString) throws JSONException {
		
		JSONObject tokenObject = new JSONObject(tokenString);

		String accessToken = tokenObject.getString("access_token");
		String refreshToken = tokenObject.getString("refresh_token");	
		String tokenType = tokenObject.getString("token_type");
		int expiresIn = tokenObject.getInt("expires_in");

		credentialsManager.setTokens(accessToken, refreshToken, tokenType, expiresIn);	
	}

	/**
	 * AsyncTask class that carry out the refreshing of access token.
	 * If passed a true argument, it calls the onAuthenticated() method of the AuthenticationInterface instance
	 *
	 */
    class RefreshTokenTask extends AsyncTask<Boolean, Void, String> {

    	boolean notify = false;
    	
    	protected String getJSONTokenString() throws ClientProtocolException, IOException {
	           HttpResponse response = doPost(TOKENS_URL, GRANT_TYPE_REFRESH);
	           String data = jsonParser.getJsonString(response.getEntity().getContent());
	           return data;
		}

		@Override
		protected String doInBackground(Boolean... params) {

			if (params.length > 0) {
				notify = params[0];
			}

			String result = null;
				try {
					String jsonTokenString = getJSONTokenString();
					getTokenDetails(jsonTokenString);
					result = "ok";
				} 
				catch (JSONException e) {
					Log.e("", "", e);
					return result;
				} catch (ClientProtocolException e) {
					Log.e("", "", e);
					return result;
				} catch (IOException e) {
					Log.e("", "", e);
					return result;
				}

			return result;
		}
		
		@Override
		protected void onPostExecute(String result) {			
			createRefreshHandler(false);
			if (notify) {
				authInterface.onAuthenticated();
			}
		}
    }	
    
    /**
	 * Helper method for executing http post request
	 * 
	 * @param url the url string
	 * @param grantType the grant type string
	 * @return the HttpResponse object
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	HttpResponse doPost(String url, String grantType) throws ClientProtocolException, IOException {
		
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);
        
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
        nameValuePairs.add(new BasicNameValuePair("grant_type", grantType));
        nameValuePairs.add(new BasicNameValuePair("redirect_uri", "http://localhost/auth_return"));
        nameValuePairs.add(new BasicNameValuePair("code", authorizationCode));
        nameValuePairs.add(new BasicNameValuePair("client_id", credentialsManager.getClientID()));
        nameValuePairs.add(new BasicNameValuePair("client_secret", credentialsManager.getClientSecret()));
        nameValuePairs.add(new BasicNameValuePair("refresh_token", NetworkProvider.refreshToken));
        
        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

        HttpResponse response = httpclient.execute(httppost);
		  
		return response;  
	}

	protected void authenticated(String authorizationCode) {
		manager = null;
		this.authorizationCode = authorizationCode;
		authInterface.onAuthenticated();
		createRefreshHandler(false);
	}
    
	protected void failedToAuthenticate() {
		manager = null;
		authInterface.onAuthenticationFail();
	}
}
