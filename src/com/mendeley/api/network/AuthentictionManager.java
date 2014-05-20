package com.mendeley.api.network;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.mendeley.api.R;
import com.mendeley.api.network.interfaces.AuthenticationInterface;
import com.mendeley.api.util.Utils;

public class AuthentictionManager extends APICallManager {
	
	WebView webView;	
	String authorizationCode;	
	Handler refreshHandler;	
	Dialog loginDialog;
	
	AuthenticationInterface authInterface;
	
	protected AuthentictionManager (Context context, AuthenticationInterface authInterface) {
		super(context); 
		this.authInterface = authInterface;
	}
	
	protected boolean hasCredentials() {
		return credentialsManager.hasCredentials();
	}
	
	public void authenticate() {
		if (hasCredentials()) {
			createRefreshHandler(true);
		} else {
			createDialog();		
			this.webView.setWebViewClient(new MendeleyWebViewClient());
			this.webView.loadUrl(getOauth2URL());
	
		    loginDialog.show();
		}
	}
		
	private void createDialog() {
		loginDialog = new Dialog(context);
		loginDialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
		loginDialog.setContentView(R.layout.dialog_layout);
		loginDialog.setTitle("Log in Mendeley");
		loginDialog.setCancelable(true);

		loginDialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.ic_launcher);

    	webView = (WebView) loginDialog.findViewById(R.id.dialogWebView);

    	((Button)loginDialog.findViewById(R.id.cancelButton)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				loginDialog.hide();
			}
		});
    } 
	  
	private void createRefreshHandler(boolean notify) {

		long delayMillis = (long)((NetworkProvider.expiresIn * 0.9) * 1000);
		refreshHandler = new Handler();
		
		if (notify) {
			refreshHandler.postDelayed(refreshRunnableNotify, 1);
		} else {
			refreshHandler.postDelayed(refreshRunnable, delayMillis);
		}
	}

	Runnable refreshRunnableNotify = new Runnable() {	
		@Override
		public void run() {
			refreshToken(true);
		}
	};

	Runnable refreshRunnable = new Runnable() {	
		@Override
		public void run() {
			refreshToken(false);
		}
	};
	
	
	public void refreshToken(boolean notify) {
		new RefreshTokenTask().execute(notify);
	}
		
	private String getOauth2URL() {
		
		StringBuffer urlString = new StringBuffer(OUATH2_URL);
		
		urlString
		.append("?").append("grant_type=").append(GRANT_TYPE_AUTH)
		.append("&").append("redirect_uri=").append(REDIRECT_URI)
		.append("&").append("scope=").append(SCOPE)
		.append("&").append("response_type=").append(RESPONSE_TYPE)
		.append("&").append("client_id=").append(credentialsManager.getClientID());
		
		return urlString.toString();
	}
		
    private class MendeleyWebViewClient extends WebViewClient {
    	
    	@Override
    	public boolean shouldOverrideUrlLoading (WebView view, String url) {

    		new AuthenticateTask().execute(url);
			return true;    		
    	}
    }
        
	private void getTokenDetails(String tokenString) throws JSONException {
		
		JSONObject tokenObject = new JSONObject(tokenString);

		String accessToken = tokenObject.getString("access_token");
		String refreshToken = tokenObject.getString("refresh_token");	
		String tokenType = tokenObject.getString("token_type");
		int expiresIn = tokenObject.getInt("expires_in");

		credentialsManager.setTokens(accessToken, refreshToken, tokenType, expiresIn);	
	}
	    
    class AuthenticateTask extends AsyncTask<String, Void, String> {

    	protected String getJSONTokenString(String authorizationCode) throws ClientProtocolException, IOException {
    		HttpResponse response = doPost(TOKENS_URL, GRANT_TYPE_AUTH, authorizationCode);
    		String data = getJsonString(response.getEntity().getContent());
	           
    		return data;
		}
    	
    	protected String getAuthorizationCode(String authReturnUrl) {
    		
    		String AuthorizationCode = null;
			int index = authReturnUrl.indexOf("code=");	       			
	        if (index != -1) {
	        	index += 5;
	        	AuthorizationCode = authReturnUrl.substring(index);
	        }
			
			return AuthorizationCode;
    	}
    	
		@Override
		protected String doInBackground(String... params) {
			
			String result = null;
			
			authorizationCode = getAuthorizationCode(params[0]);			
			if (authorizationCode != null) {
				try {
					String jsonTokenString = getJSONTokenString(authorizationCode);
					getTokenDetails(jsonTokenString);
					result = "ok";
					
				} catch (IOException e) {
					Log.e("", "", e);

				} catch (JSONException e) {
					Log.e("", "", e);
				}
			}
			
			return result;
		}
		
		@Override
		protected void onPostExecute(String result) {
			if (result == null) {
				authInterface.onAuthenticationFail();
			}
			else {
				loginDialog.hide();
				authInterface.onAuthenticated();
				createRefreshHandler(false);
			}
		}    
    }
    
    
    class RefreshTokenTask extends AsyncTask<Boolean, Void, String> {

    	boolean notify = false;
    	
    	protected String getJSONTokenString(String AuthorizationCode) throws ClientProtocolException, IOException {
	           HttpResponse response = doPost(TOKENS_URL, GRANT_TYPE_REFRESH, AuthorizationCode);
	           String data = getJsonString(response.getEntity().getContent());
	           
	           return data;
		}

		@Override
		protected String doInBackground(Boolean... params) {

			if (params.length > 0) {
				notify = params[0];
			}

			String result = null;
				try {
					String jsonTokenString = getJSONTokenString(authorizationCode);
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
}
