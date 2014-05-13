package com.mendeley.api.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.mendeley.api.network.interfaces.MendeleyAPICallsInterface;

import android.content.Context;

public abstract class APICallManager {

	final static String TOKENS_URL = "https://api-oauth2.mendeley.com/oauth/token";
	final static String OUATH2_URL = "https://api-oauth2.mendeley.com/oauth/authorize";
	final static String GRANT_TYPE_AUTH = "authorization_code";
	final static String GRANT_TYPE_REFRESH = "refresh_token";
	final static String REDIRECT_URI = "http://localhost/auth_return";
	final static String SCOPE = "all";
	final static String RESPONSE_TYPE = "code";
	
	Context context;
	CredentialsManager credentialsManager;
	
	MendeleyAPICallsInterface appInterface;
	
	protected APICallManager(Context context) {
		this.context = context;
		
		appInterface = (MendeleyAPICallsInterface) context;
		
		credentialsManager = new CredentialsManager(context);
	}
	 
	HttpResponse doGet(String url) throws ClientProtocolException, IOException {
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(url);
		httpget.setHeader("Authorization", "Bearer " + DefaultNetworkProvider.accessToken);
		
		HttpResponse response = httpclient.execute(httpget);
		  
		return response;  
	}
	
	HttpResponse doPost(String url, String grantType, String authorizationCode) throws ClientProtocolException, IOException {
		
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(url);
        
        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(5);
        nameValuePairs.add(new BasicNameValuePair("grant_type", grantType));
        nameValuePairs.add(new BasicNameValuePair("redirect_uri", "http://localhost/auth_return"));
        nameValuePairs.add(new BasicNameValuePair("code", authorizationCode));
        nameValuePairs.add(new BasicNameValuePair("client_id", credentialsManager.getClientID()));
        nameValuePairs.add(new BasicNameValuePair("client_secret", credentialsManager.getClientSecret()));
        if (grantType.equals(GRANT_TYPE_REFRESH)) {
        	nameValuePairs.add(new BasicNameValuePair("refresh_token", DefaultNetworkProvider.refreshToken));
        }
        
        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

        HttpResponse response = httpclient.execute(httppost);
		  
		return response;  
	}
	
	String getJsonString(InputStream stream) throws IOException {
		
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
}
