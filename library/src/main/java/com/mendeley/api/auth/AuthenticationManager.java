package com.mendeley.api.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.mendeley.api.activity.SignInActivity;
import com.mendeley.api.callbacks.RequestHandle;
import com.mendeley.api.exceptions.AuthenticationException;
import com.mendeley.api.exceptions.MendeleyException;
import com.mendeley.api.impl.BaseMendeleySdk;
import com.mendeley.api.network.JsonParser;
import com.mendeley.api.util.Utils;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.mendeley.api.impl.BaseMendeleySdk.Command;

public class AuthenticationManager implements AccessTokenProvider {
    public static final String TOKENS_URL = "https://api.mendeley.com/oauth/token";
    public static final String GRANT_TYPE_AUTH = "authorization_code";
    public static final String GRANT_TYPE_REFRESH = "refresh_token";
    public static final String GRANT_TYPE_PASSWORD = "password";
    public static final String SCOPE = "all";
    public static final String RESPONSE_TYPE = "code";

    // Only use tokens which don't expire in the next 5 mins:
    private static final int MIN_TOKEN_VALIDITY_SEC = 300;

    private final String username;
    private final String password;

    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;

	private final CredentialsManager credentialsManager;
	private final AuthenticationInterface authInterface;
	
	private static final String TAG = AuthenticationManager.class.getSimpleName();

    private Handler refreshHandler;

    public AuthenticationManager(Context context, AuthenticationInterface authInterface,
                                 String clientId, String clientSecret, String redirectUri) {
        this.username = null;
        this.password = null;
        this.authInterface = authInterface;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;

        SharedPreferences preferences = context.getSharedPreferences("auth", Context.MODE_PRIVATE);
        credentialsManager = new SharedPreferencesCredentialsManager(preferences);
    }

    public AuthenticationManager(String username, String password,
                                 AuthenticationInterface authInterface,
                                 String clientId, String clientSecret, String redirectUri) {
        this.username = username;
        this.password = password;
        this.authInterface = authInterface;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;

        credentialsManager = new InMemoryCredentialsManager();
    }

    public boolean isSignedIn() {
        return credentialsManager.hasCredentials();
    }

    public void signIn(Activity activity) {
        if (isSignedIn()) {
            authenticated(true);
            return;
        }
        if (username == null) {
            Intent intent = new Intent(activity, SignInActivity.class);
            activity.startActivity(intent);
        } else {
            new PasswordAuthenticationTask().execute();
        }
    }

    public void clearCredentials() {
        credentialsManager.clearCredentials();
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public String getAccessToken() {
        return credentialsManager.getAccessToken();
    }

    /**
     * Extracts the token details from the token string and sends them to the CredentialManager.
     *
     * @param tokenString
     * @throws JSONException
     */
    public void setTokenDetails(String tokenString) throws JSONException {
        credentialsManager.setCredentials(tokenString);
    }

    public void authenticated(boolean manualSignIn) {
        authInterface.onAuthenticated(manualSignIn);
    }

    public void failedToAuthenticate() {
        authInterface.onAuthenticationFail();
    }

    /**
     * Checks if the current access token will expire soon (or isn't valid at all).
     */
    public boolean willExpireSoon() {
        if (!credentialsManager.hasCredentials() || credentialsManager.getExpiresAt() == null) {
            return true;
        }
        Date now = new Date();
        Date expires = null;
        try {
            expires = Utils.dateFormat.parse(credentialsManager.getExpiresAt());
        } catch (ParseException e) {
            return true;
        }

        long timeToExpiryMs = expires.getTime() - now.getTime();
        long timeToExpirySec = TimeUnit.MILLISECONDS.toSeconds(timeToExpiryMs);

        return timeToExpirySec < MIN_TOKEN_VALIDITY_SEC;
    }

    /**
     * Start the refresh process, and return a provisional RequestHandle
     */
    public RequestHandle refreshToken(Command command) {
        RefreshTokenTask refreshTask = new RefreshTokenTask(command);
        RequestHandle requestHandle = refreshTask.getRequestHandle();
        refreshTask.execute();
        return requestHandle;
    }

    /**
     * Refresh the token. Blocks until done.
     */
    public void refreshToken() throws MendeleyException {
        HttpResponse response = null;
        try {
            response = doRefreshPost();
            String jsonTokenString = JsonParser.getJsonString(response.getEntity().getContent());
            setTokenDetails(jsonTokenString);
        } catch (IOException e) {
            throw new AuthenticationException("Cannot refresh token");
        } catch (JSONException e) {
            throw new AuthenticationException("Cannot refresh token");
        }
    }

    /**
	 * Task to refresh the access token.
	 */
    private class RefreshTokenTask extends AsyncTask<Void, Void, Boolean> {
        private Command command;
        private ChainedRequestHandle chainedRequestHandle;

        public RefreshTokenTask(Command command) {
            this.command = command;
            chainedRequestHandle = new ChainedRequestHandle();
        }

        public RequestHandle getRequestHandle() {
            return chainedRequestHandle;
        }

		@Override
		protected Boolean doInBackground(Void... params) {
			boolean result = false;
            try {
                HttpResponse response = doRefreshPost();
                String jsonTokenString = JsonParser.getJsonString(response.getEntity().getContent());
                setTokenDetails(jsonTokenString);
                result = true;
            } catch (IOException ignored) {
            } catch (JSONException ignored) {
            }
            return result;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
            if (!chainedRequestHandle.isCancelled()) {
                RequestHandle innerRequestHandle = command.exec();
                chainedRequestHandle.setInnerRequestHandle(innerRequestHandle);
            }
		}
    }

    private class ChainedRequestHandle implements RequestHandle {
        private boolean cancelled;
        private RequestHandle innerRequestHandle;

        public ChainedRequestHandle() {
            cancelled = false;
        }

        public boolean isCancelled() {
            return cancelled;
        }

        public void setInnerRequestHandle(RequestHandle handle) {
            innerRequestHandle = handle;
        }

        @Override
        public void cancel() {
            cancelled = true;
            if (innerRequestHandle != null) {
                innerRequestHandle.cancel();
            }
        }
    }

    /**
     * AsyncTask class that obtains an access token from username and password.
     */
    private class PasswordAuthenticationTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... params) {
            String result = null;
            try {
                HttpResponse response = doPasswordPost();
                String jsonTokenString = JsonParser.getJsonString(response.getEntity().getContent());
                setTokenDetails(jsonTokenString);
                result = "ok";
            } catch (IOException e) {
                Log.e(TAG, "", e);
            } catch (JSONException e) {
                Log.e(TAG, "", e);
            }

            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            if (result == null) {
                failedToAuthenticate();
            } else {
                authenticated(true);
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
        nameValuePairs.add(new BasicNameValuePair("redirect_uri", redirectUri));
        nameValuePairs.add(new BasicNameValuePair("client_id", clientId));
        nameValuePairs.add(new BasicNameValuePair("client_secret", clientSecret));
        nameValuePairs.add(new BasicNameValuePair("refresh_token", credentialsManager.getRefreshToken()));
        
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
}
