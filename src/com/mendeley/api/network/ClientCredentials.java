package com.mendeley.api.network;

public class ClientCredentials {
    public final String clientId;
    public final String clientSecret;
    public final String redirectUri;

    public ClientCredentials(String clientId, String clientSecret, String redirectUri) {
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
    }
}
