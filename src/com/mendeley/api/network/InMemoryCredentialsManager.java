package com.mendeley.api.network;

import org.json.JSONException;
import org.json.JSONObject;

public class InMemoryCredentialsManager implements CredentialsManager {
    private String accessToken; // null if not set
    private String refreshToken;
    private String tokenType;
    private int expiresIn;
    private String expiresAt;

    @Override
    public void setTokenDetails(String tokenString) throws JSONException {
        JSONObject tokenObject = new JSONObject(tokenString);

        accessToken = tokenObject.getString("access_token");
        refreshToken = tokenObject.getString("refresh_token");
        tokenType = tokenObject.getString("token_type");
        expiresIn = tokenObject.getInt("expires_in");

        expiresAt = SharedPreferencesCredentialsManager.generateExpiresAtFromExpiresIn(expiresIn);
    }

    @Override
    public void clearCredentials() {
        accessToken = null;

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
        return expiresIn;
    }

    @Override
    public String getExpiresAt() {
        return expiresAt;
    }

    @Override
    public String getRefreshToken() {
        return refreshToken;
    }

    private String getAccessToken() {
        return accessToken;
    }
}
