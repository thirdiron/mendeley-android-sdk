package com.mendeley.api.network;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import com.mendeley.api.network.interfaces.AuthenticationInterface;
import com.mendeley.api.R;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;

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
	
	public void authenticate() {
		
		createDialog();		
		this.webView.setWebViewClient(new MendeleyWebViewClient());
		this.webView.loadUrl(getOauth2URL());

	    loginDialog.show();
	}
		
	private void createDialog() {
		loginDialog = new Dialog(context);
		loginDialog.requestWindowFeature(Window.FEATURE_LEFT_ICON);
		loginDialog.setContentView(R.layout.dialog_layout);
		loginDialog.setTitle("Log in Mendeley");
		loginDialog.setCancelable(false);

		loginDialog.setFeatureDrawableResource(Window.FEATURE_LEFT_ICON, R.drawable.ic_launcher);

    	webView = (WebView) loginDialog.findViewById(R.id.dialogWebView);
    }
	  
	private void createRefreshHandler() {
		long delayMillis = (long)((DefaultNetworkProvider.expiresIn * 0.9) * 1000);
		Log.e("", "creating handler - " + delayMillis);
		refreshHandler = new Handler();
		refreshHandler.postDelayed(refreshRunnable, delayMillis);
	}

	private Runnable refreshRunnable = new Runnable() {	
		@Override
		public void run() {
			refreshToken();
		}
	};
	
	
	public void refreshToken() {
		new RefreshTokenTask().execute("");
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
		
		DefaultNetworkProvider.accessToken = tokenObject.getString("access_token");
		DefaultNetworkProvider.tokenType = tokenObject.getString("token_type");
		DefaultNetworkProvider.expiresIn = tokenObject.getInt("expires_in");
		DefaultNetworkProvider.refreshToken = tokenObject.getString("refresh_token");		
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
				createRefreshHandler();
			}
		}    
    }
    
    
    class RefreshTokenTask extends AsyncTask<String, Void, String> {

    	protected String getJSONTokenString(String AuthorizationCode) throws ClientProtocolException, IOException {
	           HttpResponse response = doPost(TOKENS_URL, GRANT_TYPE_REFRESH, AuthorizationCode);
	           String data = getJsonString(response.getEntity().getContent());
	           
	           return data;
		}

		@Override
		protected String doInBackground(String... params) {
			
			String result = null;
			
			if (authorizationCode == null) {
				return result;
			} else {
				try {
					String jsonTokenString = getJSONTokenString(authorizationCode);
					getTokenDetails(jsonTokenString);
					result = "ok";
					
					Log.e("refresh", jsonTokenString);
					
				} catch (IOException e) {
					Log.e("", "", e);
					return result;
				} 
				catch (JSONException e) {
					Log.e("", "", e);
					return result;
				}
			}			
			return result;
		}
		
		@Override
		protected void onPostExecute(String result) {
			Log.e("", "refresh result: " + result);
			
			createRefreshHandler();
		}
    }	
}
