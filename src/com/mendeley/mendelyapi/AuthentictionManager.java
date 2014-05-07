package com.mendeley.mendelyapi;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
	
	protected AuthentictionManager (Context context) {
		super(context); 
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
	
	
	private void sendBroadcast(int type) {
	    Intent intent = new Intent(MendeleyAPINetworkProvider.AUTHENTICATEED_FILTER);
		intent.putExtra(MendeleyAPINetworkProvider.INTENT_TYPE, type);
		context.sendBroadcast(intent); 
	}

	  
	public void createRefreshHandler() {
		sendBroadcast(MendeleyAPINetworkProvider.INTENT_REFRESH);
		long delayMillis = (long)((MendeleyAPINetworkProvider.expiresIn * 0.9) * 1000);
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
		
		MendeleyAPINetworkProvider.accessToken = tokenObject.getString("access_token");
		MendeleyAPINetworkProvider.tokenType = tokenObject.getString("token_type");
		MendeleyAPINetworkProvider.expiresIn = tokenObject.getInt("expires_in");
		MendeleyAPINetworkProvider.refreshToken = tokenObject.getString("refresh_token");		
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

				} catch (JSONException e) {

				}
			}
			
			return result;
		}
		
		@Override
		protected void onPostExecute(String result) {
			if (result == null) {
				sendBroadcast(MendeleyAPINetworkProvider.INTENT_LOGIN);
				Log.e("", "could not authenticate");
			}
			else {
				sendBroadcast(MendeleyAPINetworkProvider.INTENT_AUTHENTICATED);
				loginDialog.hide();
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
