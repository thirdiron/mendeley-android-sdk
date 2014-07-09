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

import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class is responsible for storing and retrieving the credentials when needed.
 * Credentials are kept in SharedPreferences.
 */
public class CredentialsManager {
    // Shared preferences keys:
	private static final String ACCESS_TOKEN = "accessToken";
	private static final String REFRESH_TOKEN = "refreshToken";
	private static final String EXPIRES_IN = "expiresIn";
	private static final String EXPIRES_AT = "expiresAt";
	private static final String TOKEN_TYPE = "tokenType";
 
	private SharedPreferences preferences;
 
	protected CredentialsManager(SharedPreferences preferences) {
        this.preferences = preferences;
	}

    protected void setTokenDetails(String tokenString) throws JSONException {
        JSONObject tokenObject = new JSONObject(tokenString);

        String accessToken = tokenObject.getString("access_token");
        String refreshToken = tokenObject.getString("refresh_token");
        String tokenType = tokenObject.getString("token_type");
        int expiresIn = tokenObject.getInt("expires_in");

        setTokens(accessToken, refreshToken, tokenType, expiresIn);
    }
	
	/**
	 * Removing the credentials from the SharedPreferences as well as the NetworkProvider static string objects.
	 */
	protected void clearCredentials() {
		Editor editor = preferences.edit();
		editor.remove(ACCESS_TOKEN);
		editor.remove(REFRESH_TOKEN);
		editor.remove(EXPIRES_AT);
		editor.remove(EXPIRES_IN);
		editor.remove(TOKEN_TYPE);
		editor.commit();
		
		NetworkProvider.accessToken = null;
	}
 
	/**
	 * Checking if all requires credentials exists, 
	 * if true update the value of the appropriate NetworkProvider static string objects. 
	 * 
	 * @return true of all credentials exist, false otherwise.
	 */
	protected boolean checkCredentialsAndCopyToNetworkProvider() {
		boolean hasCredentials =
				getAccessToken() != null &&
				getRefreshToken() != null && 
				getExpiresIn() != -1 && 
				getTokenType() != null;

		if (hasCredentials) {
			NetworkProvider.accessToken = getAccessToken();
		}
		
		return hasCredentials;
	}

    /**
     * @return the expires in integer or -1 if it does not exist.
     */
    protected int getExpiresIn() {
        return preferences.getInt(EXPIRES_IN, -1);
    }

    /**
     * @return the expires in string value or null if it does not exist.
     */
    protected String getExpiresAt() {
        return preferences.getString(EXPIRES_AT, null);
    }

    /**
     * @return the refresh token string or null if it does not exist.
     */
    protected String getRefreshToken() {
        return preferences.getString(REFRESH_TOKEN, null);
    }

    /**
     * @return the access token string or null if it does not exist.
     */
    private String getAccessToken() {
        return preferences.getString(ACCESS_TOKEN, null);
    }

    /**
     * @return the token type string or null if it does not exist.
     */
    private String getTokenType() {
        return preferences.getString(TOKEN_TYPE, null);
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
        Calendar c = Calendar.getInstance();
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
    }
}
