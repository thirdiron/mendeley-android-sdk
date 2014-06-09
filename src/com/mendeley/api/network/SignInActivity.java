package com.mendeley.api.network;

import java.io.IOException;
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
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;

import com.mendeley.api.R;

public class SignInActivity  extends Activity implements OnClickListener {
	
	final static String FORGOT_PASSWORD_URL = "http://www.mendeley.com/forgot/";
	final static String CREATE_ACCOUNT_URL = "http://www.mendeley.com/";
	
	LinearLayout dialogView;
	LinearLayout shadowView;
	boolean isDialogShowing = false;
	
	WebView webView;	
	String authorizationCode;	
	JsonParser jsonParser = new JsonParser();	
	CredentialsManager credentialsManager;	
	AuthenticationManager manager;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
    	credentialsManager = new CredentialsManager(this);

        setContentView(R.layout.splash_layout);
        dialogView = (LinearLayout) findViewById(R.id.dialogView);
        shadowView = (LinearLayout) findViewById(R.id.shadow);
        
        ((Button) findViewById(R.id.signinButton)).setOnClickListener(this);
        ((Button) findViewById(R.id.signupButton)).setOnClickListener(this);
    }
    
    /**
     * Loading the url in the webview and showing the dialog.
     */
    private void buildDialog() {

		webView = (WebView) dialogView.findViewById(R.id.dialogWebView);
		this.webView.getSettings().setJavaScriptEnabled(true);
		this.webView.setVerticalScrollBarEnabled(true);
		this.webView.setHorizontalScrollBarEnabled(true);
		
	       webView.setVerticalScrollBarEnabled(true);
	        webView.setHorizontalScrollBarEnabled(true);
	        webView.requestFocusFromTouch();

	        webView.getSettings().setJavaScriptEnabled(true);
	        webView.getSettings().setUseWideViewPort(true);                                                         
	        webView.getSettings().setLoadWithOverviewMode(true);
	        webView.getSettings().setBuiltInZoomControls(true);
		
		this.webView.setWebViewClient(new MendeleyWebViewClient());
		this.webView.loadUrl(getOauth2URL());

		showDialog();
    }
	
	/**
	 * AsyncTask class that carry out the authentication task and send a callback 
	 * to the AuthenticationInterface instance upon successful or failed authentication.
	 *
	 */
    class AuthenticateTask extends AsyncTask<String, Void, String> {

    	protected String getJSONTokenString(String authorizationCode) throws ClientProtocolException, IOException {
    		HttpResponse response = doPost(AuthenticationManager.TOKENS_URL, AuthenticationManager.GRANT_TYPE_AUTH, authorizationCode);
    		String data = jsonParser.getJsonString(response.getEntity().getContent());
	           
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
				
				AuthenticationManager.getInstance().failedToAuthenticate();
	        	finish();
			}
			else {
				hideDialog();

				AuthenticationManager.getInstance().authenticated(authorizationCode);
	        	finish();
			}
		}    
    }
    
	private void showDialog() {
		shadowView.setVisibility(View.VISIBLE);
		dialogView.setVisibility(View.VISIBLE);
		isDialogShowing = true;
	}
	
	private void hideDialog() {
		shadowView.setVisibility(View.INVISIBLE);
		dialogView.setVisibility(View.INVISIBLE);
		isDialogShowing = false;
	}
    
	/**
	 * A WebViewClient that starts the AuthenticationTask when a new url is loaded.
	 *
	 */
    private class MendeleyWebViewClient extends WebViewClient {
    	
    	@Override
    	public boolean shouldOverrideUrlLoading (WebView view, String url) {

    		if (url.equals(FORGOT_PASSWORD_URL)) {
    			openUrlInBrowser(url);
    		} else {
    			new AuthenticateTask().execute(url);
    		}
			return true;    		
    	}
    }
    
    
    private void openUrlInBrowser(String url) {
    	Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url)); 
    	startActivity(intent);
    }
    
    
	/**
	 * Creating and return the Oauth2 url string.
	 * 
	 * @return the url string
	 */
	private String getOauth2URL() {
		
		StringBuffer urlString = new StringBuffer(AuthenticationManager.OUATH2_URL);
		
		urlString
		.append("?").append("grant_type=").append(AuthenticationManager.GRANT_TYPE_AUTH)
		.append("&").append("redirect_uri=").append(AuthenticationManager.REDIRECT_URI)
		.append("&").append("scope=").append(AuthenticationManager.SCOPE)
		.append("&").append("response_type=").append(AuthenticationManager.RESPONSE_TYPE)
		.append("&").append("client_id=").append(credentialsManager.getClientID());
		
		return urlString.toString();
	}

	
	/**
	 * Helper method for executing http post request
	 * 
	 * @param url the url string
	 * @param grantType the grant type string
	 * @param authorizationCode the authorisation code string
	 * @return the HttpResponse object
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	HttpResponse doPost(String url, String grantType, String authorizationCode) throws ClientProtocolException, IOException {
		
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);
        
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
        nameValuePairs.add(new BasicNameValuePair("grant_type", grantType));
        nameValuePairs.add(new BasicNameValuePair("redirect_uri", "http://localhost/auth_return"));
        nameValuePairs.add(new BasicNameValuePair("code", authorizationCode));
        nameValuePairs.add(new BasicNameValuePair("client_id", credentialsManager.getClientID()));
        nameValuePairs.add(new BasicNameValuePair("client_secret", credentialsManager.getClientSecret()));
        
        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

        HttpResponse response = httpclient.execute(httppost);
		  
		return response;  
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

	@Override
	public void onBackPressed() {
	    if (isDialogShowing) {
	    	hideDialog();
	    } else {
	    	  moveTaskToBack(true);
	    }
	}
	
	@Override
	public void onClick(View v) {
		
		int id = v.getId();
		
		if (id == R.id.signinButton) {
			buildDialog();
			
		} else if (id == R.id.signupButton) {
			openUrlInBrowser(CREATE_ACCOUNT_URL);
		}
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {

		savedInstanceState.putBoolean("isDialogShowing", isDialogShowing);
		super.onSaveInstanceState(savedInstanceState);
	}
	
	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		
		isDialogShowing = savedInstanceState.getBoolean("isDialogShowing");
		if (isDialogShowing) {
			buildDialog();
			showDialog();
		}
	}
}