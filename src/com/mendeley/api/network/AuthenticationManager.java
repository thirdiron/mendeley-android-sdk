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
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.mendeley.api.network.interfaces.AuthenticationInterface;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;

/**
 * This class is responsible for authenticating the user, 
 * using a WebView to display the authentication web page.
 */
public class AuthenticationManager implements Serializable {
	private static AuthenticationManager authManager;
	private static final long serialVersionUID = 1L;

	static final String TOKENS_URL = "https://api-oauth2.mendeley.com/oauth/token";
	static final String GRANT_TYPE_AUTH = "authorization_code";
	static final String GRANT_TYPE_REFRESH = "refresh_token";
    static final String GRANT_TYPE_PASSWORD = "password";
	static final String REDIRECT_URI = "http://localhost/auth_return";
	static final String SCOPE = "all";
	static final String RESPONSE_TYPE = "code";

	private final Context context;
    private final String username;
    private final String password;
    private final String clientId;
    private final String clientSecret;

	final CredentialsManager credentialsManager;
	final AuthenticationInterface authInterface;

    private Handler refreshHandler;

	/**
	 *  The constructor takes context which will be used for displaying the WebView
	 *  and an instance of AuthenticationInterface which will be used for callbacks, 
	 *  once user has been authentication, or authentication has failed.
	 *  
	 * @param context the context object
	 * @param authInterface the AuthenticationInterface instance for callbacks
	 */
    private AuthenticationManager(Context context, AuthenticationInterface authInterface,
                                  String clientId, String clientSecret) {
        this.context = context;
        this.username = null;
        this.password = null;
        this.authInterface = authInterface;
        this.clientId = clientId;
        this.clientSecret = clientSecret;

        SharedPreferences preferences = context.getSharedPreferences("auth", Context.MODE_PRIVATE);
        credentialsManager = new CredentialsManager(preferences);
    }

    private AuthenticationManager(String username, String password,
                                  AuthenticationInterface authInterface,
                                  String clientId, String clientSecret) {
        this.context = null;
        this.username = username;
        this.password = password;
        this.authInterface = authInterface;
        this.clientId = clientId;
        this.clientSecret = clientSecret;

        credentialsManager = new CredentialsManager(null);
    }

    protected static void configure(Context context, AuthenticationInterface authInterface,
                                    String clientId, String clientSecret) {
        // TODO: Uncomment the following assertion once MendeleySDK is a singleton
        // assertNull("configure can only be called once", authManager);
        authManager = new AuthenticationManager(context, authInterface, clientId, clientSecret);
	}

    protected static void configure(String username, String password,
                                    AuthenticationInterface authInterface,
                                    String clientId, String clientSecret) {
        // TODO: Uncomment the following assertion once MendeleySDK is a singleton
        // assertNull("configure can only be called once", authManager);
        authManager = new AuthenticationManager(username, password, authInterface, clientId, clientSecret);
    }

    protected static AuthenticationManager getInstance() {
        assertNotNull("authManager must have been configured", authManager);
		return authManager;
	}

    protected String getClientId() {
        return clientId;
    }

    protected String getClientSecret() {
        return clientSecret;
    }

    /**
	 * Querying the CredentialManager if credentials are already stored on the device.
	 * 
	 * @return true if credentials exists, false otherwise.
	 */
	protected boolean checkCredentialsAndCopyToNetworkProvider() {
		return credentialsManager.checkCredentialsAndCopyToNetworkProvider();
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
		if (checkCredentialsAndCopyToNetworkProvider()) {
			createRefreshHandler(true);
		} else {
			startSignInFlow();
		}
	}

    private void startSignInFlow() {
        if (username == null) {
            startSignInActivity();
        } else {
            startAutomaticSignIn();
        }
    }

    private void startAutomaticSignIn() {
        new PasswordAuthenticationTask().execute();
    }

	/**
	 * Starting the sign in activity.
	 */
	private void startSignInActivity() {
		Intent intent = new Intent(context, SignInActivity.class);
		((Activity) context).startActivity(intent);	
	}

    /**
     * Extracts the token details from the token string and sends them to the CredentialManager.
     *
     * @param tokenString
     * @throws JSONException
     */
    public void setTokenDetails(String tokenString) throws JSONException {
        credentialsManager.setTokenDetails(tokenString);
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
	 * AsyncTask class that carries out the refreshing of access token.
	 * If passed a true argument, it calls the onAuthenticated() method of the AuthenticationInterface instance
	 */
    class RefreshTokenTask extends AsyncTask<Boolean, Void, String> {
    	private boolean notify = false;
    	
		@Override
		protected String doInBackground(Boolean... params) {
			if (params.length > 0) {
				notify = params[0];
			}

			String result = null;
				try {
                    HttpResponse response = doRefreshPost();
                    String jsonTokenString = JsonParser.getJsonString(response.getEntity().getContent());
					setTokenDetails(jsonTokenString);
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
			if (result == null) {
				authInterface.onAuthenticationFail();
			} else {
				createRefreshHandler(false);
				if (notify) {
					authInterface.onAuthenticated();
				}
			}
		}
    }

    /**
     * AsyncTask class that obtains an access token from username and password.
     */
    class PasswordAuthenticationTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            String result = null;
            try {
                HttpResponse response = doPasswordPost();
                String jsonTokenString = JsonParser.getJsonString(response.getEntity().getContent());
                setTokenDetails(jsonTokenString);
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
            if (result == null) {
                failedToAuthenticate();
            } else {
                authenticated();
            }
        }
    }

    /**
	 * Helper method for executing http post request for token refresh.
	 */
	private HttpResponse doRefreshPost() throws ClientProtocolException, IOException {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(TOKENS_URL);
        
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("grant_type", GRANT_TYPE_REFRESH));
        nameValuePairs.add(new BasicNameValuePair("redirect_uri", REDIRECT_URI));
        nameValuePairs.add(new BasicNameValuePair("client_id", clientId));
        nameValuePairs.add(new BasicNameValuePair("client_secret", clientSecret));
        nameValuePairs.add(new BasicNameValuePair("refresh_token", NetworkProvider.refreshToken));
        
        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

        HttpResponse response = httpclient.execute(httppost);
		  
		return response;  
	}

    /**
     * Helper method for executing http post request for password-based authentication.
     */
    private HttpResponse doPasswordPost() throws ClientProtocolException, IOException {
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(TOKENS_URL);

        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("grant_type", GRANT_TYPE_PASSWORD));
        nameValuePairs.add(new BasicNameValuePair("scope", SCOPE));
        nameValuePairs.add(new BasicNameValuePair("client_id", clientId));
        nameValuePairs.add(new BasicNameValuePair("client_secret", clientSecret));
        nameValuePairs.add(new BasicNameValuePair("username", username));
        nameValuePairs.add(new BasicNameValuePair("password", password));

        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

        HttpResponse response = httpclient.execute(httppost);

        return response;
    }

    protected void authenticated() {
		authInterface.onAuthenticated();
		createRefreshHandler(false);
	}
    
	protected void failedToAuthenticate() {
		authInterface.onAuthenticationFail();
	}
}
