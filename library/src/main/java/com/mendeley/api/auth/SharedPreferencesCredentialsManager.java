package com.mendeley.api.auth;

import java.util.Calendar;

import com.mendeley.api.util.Utils;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class is responsible for storing and retrieving the credentials when needed.
 * Credentials are kept in SharedPreferences.
 */
public class SharedPreferencesCredentialsManager implements CredentialsManager {
	private static final String TAG = SharedPreferencesCredentialsManager.class.getSimpleName();
	
    // Shared preferences keys:
	private static final String ACCESS_TOKEN_KEY = "accessToken";
	private static final String REFRESH_TOKEN_KEY = "refreshToken";
	private static final String EXPIRES_AT_KEY = "expiresAt";
	private static final String TOKEN_TYPE_KEY = "tokenType";
 
	private SharedPreferences preferences;
 
	protected SharedPreferencesCredentialsManager(SharedPreferences preferences) {
        this.preferences = preferences;
	}

    @Override
    public void setCredentials(String tokenString) throws JSONException {
    	String accessToken;
    	String refreshToken;
    	String tokenType;
    	int expiresIn;

    	try {
	        JSONObject tokenObject = new JSONObject(tokenString);
	
	        accessToken = tokenObject.getString("access_token");
	        refreshToken = tokenObject.getString("refresh_token");
	        tokenType = tokenObject.getString("token_type");
	        expiresIn = tokenObject.getInt("expires_in");
    	} catch(JSONException e) {
    		// If the client credentials are incorrect, the tokenString contains an error message
    		Log.e(TAG, "Error token string: " + tokenString);
    		throw e;
    	}

        storeCredentials(accessToken, refreshToken, tokenType, expiresIn);
    }

    @Override
    public void clearCredentials() {
		Editor editor = preferences.edit();
		editor.remove(ACCESS_TOKEN_KEY);
		editor.remove(REFRESH_TOKEN_KEY);
		editor.remove(EXPIRES_AT_KEY);
		editor.remove(TOKEN_TYPE_KEY);
		editor.commit();
	}
 
    @Override
    public boolean hasCredentials() {
		return getAccessToken() != null;
	}

    @Override
    public String getExpiresAt() {
        return preferences.getString(EXPIRES_AT_KEY, null);
    }

    @Override
    public String getRefreshToken() {
        return preferences.getString(REFRESH_TOKEN_KEY, null);
    }

    /**
     * @return the access token string, or null if it does not exist.
     */
    @Override
    public String getAccessToken() {
        return preferences.getString(ACCESS_TOKEN_KEY, null);
    }

    /**
     * Stores the token details in shared preferences.
     *
     * @param accessToken the access toekn string
     * @param refreshToken the refresh token string
     * @param tokenType the token type string
     * @param expiresIn the expires in value
     */
    private void storeCredentials(String accessToken, String refreshToken, String tokenType, int expiresIn) {
        String expiresAt = generateExpiresAtFromExpiresIn(expiresIn);

        Editor editor = preferences.edit();
        editor.putString(ACCESS_TOKEN_KEY, accessToken);
        editor.putString(REFRESH_TOKEN_KEY, refreshToken);
        editor.putString(TOKEN_TYPE_KEY, tokenType);
        editor.putString(EXPIRES_AT_KEY, expiresAt);
        editor.commit();
    }

    public static String generateExpiresAtFromExpiresIn(int expiresIn) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.SECOND, expiresIn);
        return Utils.dateFormat.format(c.getTime());
    }
}
