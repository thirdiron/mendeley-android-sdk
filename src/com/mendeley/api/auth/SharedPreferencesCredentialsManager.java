package com.mendeley.api.auth;

import java.util.Calendar;

import com.mendeley.api.network.NetworkProvider;
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
	private static final String ACCESS_TOKEN = "accessToken";
	private static final String REFRESH_TOKEN = "refreshToken";
	private static final String EXPIRES_IN = "expiresIn";
	private static final String EXPIRES_AT = "expiresAt";
	private static final String TOKEN_TYPE = "tokenType";
 
	private SharedPreferences preferences;
 
	protected SharedPreferencesCredentialsManager(SharedPreferences preferences) {
        this.preferences = preferences;
	}

    @Override
    public void setTokenDetails(String tokenString) throws JSONException {
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

        setTokens(accessToken, refreshToken, tokenType, expiresIn);
    }

    @Override
    public void clearCredentials() {
		Editor editor = preferences.edit();
		editor.remove(ACCESS_TOKEN);
		editor.remove(REFRESH_TOKEN);
		editor.remove(EXPIRES_AT);
		editor.remove(EXPIRES_IN);
		editor.remove(TOKEN_TYPE);
		editor.commit();
		
		NetworkProvider.accessToken = null;
	}
 
    @Override
    public boolean checkCredentialsAndCopyToNetworkProvider() {
		boolean hasCredentials = getAccessToken() != null;

		if (hasCredentials) {
			NetworkProvider.accessToken = getAccessToken();
		}
		
		return hasCredentials;
	}

    @Override
    public int getExpiresIn() {
        return preferences.getInt(EXPIRES_IN, -1);
    }

    @Override
    public String getExpiresAt() {
        return preferences.getString(EXPIRES_AT, null);
    }

    @Override
    public String getRefreshToken() {
        return preferences.getString(REFRESH_TOKEN, null);
    }

    /**
     * @return the access token string or null if it does not exist.
     */
    private String getAccessToken() {
        return preferences.getString(ACCESS_TOKEN, null);
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
    private void setTokens(String accessToken, String refreshToken, String tokenType, int expiresIn) {
        String expiresAt = generateExpiresAtFromExpiresIn(expiresIn);

        Editor editor = preferences.edit();
        editor.putString(ACCESS_TOKEN, accessToken);
        editor.putString(REFRESH_TOKEN, refreshToken);
        editor.putString(TOKEN_TYPE, tokenType);
        editor.putString(EXPIRES_AT, expiresAt);
        editor.putInt(EXPIRES_IN, expiresIn);
        editor.commit();

        NetworkProvider.accessToken = accessToken;
    }

    public static String generateExpiresAtFromExpiresIn(int expiresIn) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.SECOND, expiresIn);
        return Utils.dateFormat.format(c.getTime());
    }
}
