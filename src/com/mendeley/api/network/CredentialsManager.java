package com.mendeley.api.network;

import java.util.Calendar;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import com.mendeley.api.util.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Base64;
import android.util.Log;

/**
 * This class is responsible for storing and retrieving the credentials when needed.
 * Credentials are kept in SharedPreferences using the application context. 
 *
 */
public class CredentialsManager {
    // Shared preferences keys:
	private static final String CLIENT_ID = "ouath2ClientId";
	private static final String CLIENT_SECRET = "ouath2ClientSecret";
	private static final String ACCESS_TOKEN = "accessToken";
	private static final String REFRESH_TOKEN = "refreshToken";
	private static final String EXPIRES_IN = "expiresIn";
	private static final String EXPIRES_AT = "expiresAt";
	private static final String TOKEN_TYPE = "tokenType";
 
	private SharedPreferences preferences;
 
	/**
	 * The constructor takes a context instance which will be used to store the credentials in the SharedPreferences.
	 * 
	 * @param context the context object.
	 */
	protected CredentialsManager(Context context, String clientId, String clientSecret) {
		preferences = context.getSharedPreferences("auth", Context.MODE_PRIVATE);
		
		if (preferences.getString(CLIENT_ID, null) == null ||
    			preferences.getString(CLIENT_SECRET, null) == null) {
            setClientID(clientId);
            setClientSecret(clientSecret);
		}
	}
	
	/**
	 * Storing the token details in the shared preferences.
	 * Also storing the token details in the appropriate NetworkProvider static String objects for convenience.
	 * 
	 * @param accessToken the access toekn string
	 * @param refreshToken the refresh token string
	 * @param tokenType the token type string
	 * @param expiresIn the expires in va;ue
	 */
	protected void setTokens(String accessToken, String refreshToken, String tokenType, int expiresIn) {

		Calendar c = Calendar .getInstance();		
		c.add(Calendar.SECOND, expiresIn);
        String expiresAt = Utils.dateFormat.format(c.getTime());
		
		Editor editor = preferences.edit();
		editor.putString(ACCESS_TOKEN, accessToken);
		editor.putString(REFRESH_TOKEN, refreshToken);
		editor.putString(TOKEN_TYPE, tokenType);
		editor.putString(EXPIRES_AT, expiresAt);
		editor.putInt(EXPIRES_IN, expiresIn);
		editor.commit();

		NetworkProvider.accessToken = accessToken;
		NetworkProvider.refreshToken = refreshToken;	
		NetworkProvider.tokenType = tokenType;
		NetworkProvider.expiresIn = expiresIn;
		NetworkProvider.expiresAt = expiresAt;
	}
	
	/**
	 * @return the access token string or null if it does not exist.
	 */
	protected String getAccessToken() {
		return preferences.getString(ACCESS_TOKEN, null);
	}
	
	/**
	 * @return the refresh token string or null if it does not exist.
	 */
	protected String getRefreshToken() {
		return preferences.getString(REFRESH_TOKEN, null);
	}
	
	
	/**
	 * @return the expires in string value or null if it does not exist.
	 */
	protected String getExpiresAt() {
		return preferences.getString(EXPIRES_AT, null);
	}
	
	/**
	 * @return the expires in integer or -1 if it does not exist.
	 */
	protected int getExpiresIn() {
		return preferences.getInt(EXPIRES_IN, -1);
	}
	
	/**
	 * @return the token type string or null if it does not exist.
	 */
	protected String getTokenType() {
		return preferences.getString(TOKEN_TYPE, null);
	}
	
	/**
	 * Removing the credentials from the SharedPreferences as well as the NetworkProvider static string objects.
	 */
	protected void clearCredentials() {
		Editor editor = preferences.edit();
		editor.remove(CLIENT_ID);
		editor.remove(CLIENT_SECRET);
		editor.remove(ACCESS_TOKEN);
		editor.remove(REFRESH_TOKEN);
		editor.remove(EXPIRES_AT);
		editor.remove(EXPIRES_IN);
		editor.remove(TOKEN_TYPE);
		editor.commit();
		
		NetworkProvider.accessToken = null;
		NetworkProvider.refreshToken = null;
		NetworkProvider.tokenType = null;
		NetworkProvider.expiresAt = null;
		NetworkProvider.expiresIn = -1;
	}
 
	/**
	 * Checking if all requires credentials exists, 
	 * if true update the value of the appropriate NetworkProvider static string objects. 
	 * 
	 * @return true of all credentials exist, false otherwise.
	 */
	protected boolean hasCredentials() {

		boolean hasCredentials = getClientID() != null && 
				getClientSecret() != null && 
				getAccessToken() != null &&
				getRefreshToken() != null && 
				getExpiresIn() != -1 && 
				getTokenType() != null;

		if (hasCredentials) {
			NetworkProvider.accessToken = getAccessToken();
			NetworkProvider.refreshToken = getRefreshToken();
			NetworkProvider.tokenType = getTokenType();
			NetworkProvider.expiresIn = getExpiresIn();
			NetworkProvider.expiresAt = getExpiresAt();
		}
		
		return hasCredentials;
	}
	
	/**
	 * Adding the client id credential to the SharedPreferences
	 * 
	 * @param ouath2ClientId the client id string
	 */
	protected void setClientID(String ouath2ClientId) {
		Editor editor = preferences.edit();
		editor.putString(CLIENT_ID, ouath2ClientId);
		editor.commit();
	}
 
	/**
	 * Adding the client secret to the SharedPreferences
	 * 
	 * @param ouath2ClientSecret the client secret string
	 */
	protected void setClientSecret(String ouath2ClientSecret) {
		Editor editor = preferences.edit();
		editor.putString(CLIENT_SECRET, ouath2ClientSecret);
		editor.commit();
	}
 
	/**
	 * @return the client id string
	 */
	protected String getClientID() {
		return preferences.getString(CLIENT_ID, null);
	}
 
	/**
	 * @return the client secret string
	 */
	protected String getClientSecret() {
		return preferences.getString(CLIENT_SECRET, null);
	}
}
