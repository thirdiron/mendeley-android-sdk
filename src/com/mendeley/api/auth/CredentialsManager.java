package com.mendeley.api.auth;

import org.json.JSONException;

public interface CredentialsManager {
    public void setTokenDetails(String tokenString) throws JSONException;

    /**
     * Removes the credentials from the SharedPreferences as well as the NetworkProvider static string objects.
     */
    public void clearCredentials();

    /**
     * Checks if all required credentials exist.
     * If so, updates the value of the appropriate NetworkProvider static string objects.
     *
     * @return true of all credentials exist, false otherwise.
     */
    public boolean checkCredentialsAndCopyToNetworkProvider();

    /**
     * @return the expires in integer or -1 if it does not exist.
     */
    public int getExpiresIn();

    /**
     * @return the expires in string value or null if it does not exist.
     */
    public String getExpiresAt();

    /**
     * @return the refresh token string or null if it does not exist.
     */
    public String getRefreshToken();
}
