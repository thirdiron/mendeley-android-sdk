package com.mendeley.api.auth;

import org.json.JSONException;
import org.json.JSONObject;

public class InMemoryCredentialsManager implements CredentialsManager {
    private String accessToken; // null if not set
    private String refreshToken;
    private String tokenType;
    private String expiresAt;

    @Override
    public void setCredentials(String tokenString) throws JSONException {
        JSONObject tokenObject = new JSONObject(tokenString);

        accessToken = tokenObject.getString("access_token");
        refreshToken = tokenObject.getString("refresh_token");
        tokenType = tokenObject.getString("token_type");
        int expiresIn = tokenObject.getInt("expires_in");

        expiresAt = SharedPreferencesCredentialsManager.generateExpiresAtFromExpiresIn(expiresIn);
    }

    @Override
    public void clearCredentials() {
        accessToken = null;
    }

    @Override
    public boolean hasCredentials() {
        return accessToken != null;
    }

    @Override
    public String getExpiresAt() {
        return expiresAt;
    }

    @Override
    public String getRefreshToken() {
        return refreshToken;
    }

    @Override
    public String getAccessToken() {
        return accessToken;
    }
}
