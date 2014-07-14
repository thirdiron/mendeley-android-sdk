package com.mendeley.api.auth;

public class UserCredentials {
    public final String username;
    public final String password;

    public UserCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
