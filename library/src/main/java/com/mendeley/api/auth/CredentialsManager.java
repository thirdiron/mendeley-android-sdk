package com.mendeley.api.auth;

import org.json.JSONException;

public interface CredentialsManager {
    public void setCredentials(String tokenString) throws JSONException;

    /**
     * Removes the credentials from the SharedPreferences as well as the NetworkProvider static string objects.
     */
    public void clearCredentials();

    /**
     * Check if required credentials exist.
     */
    public boolean hasCredentials();

    /**
     * @return the expires in string value or null if it does not exist.
     */
    public String getExpiresAt();

    /**
     * @return the refresh token string or null if it does not exist.
     */
    public String getRefreshToken();

    /**
     * @return the access token string or null if it does not exist.
     */
    public String getAccessToken();
}
