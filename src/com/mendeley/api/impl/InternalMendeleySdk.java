package com.mendeley.api.impl;

import com.mendeley.api.ClientCredentials;
import com.mendeley.api.auth.AuthenticationManager;
import com.mendeley.api.auth.UserCredentials;
import com.mendeley.api.callbacks.MendeleySignInInterface;

/**
 * Internal version of MendeleySdk.
 * <p>
 * Developer applications should not use this constructor; this is only required for unit
 * testing the SDK, in which sign is handled via the resource owner password flow.
 */
public class InternalMendeleySdk extends BaseMendeleySdk {
    private static InternalMendeleySdk instance;

    private InternalMendeleySdk() {}

    public static InternalMendeleySdk getInstance() {
        if (instance == null) {
            instance = new InternalMendeleySdk();
        }
        return instance;
    }

    public void signIn(MendeleySignInInterface signInCallback,
                       ClientCredentials clientCredentials, UserCredentials userCredentials)  {
        this.mendeleySignInInterface = signInCallback;
        authenticationManager = new AuthenticationManager(
                userCredentials.username,
                userCredentials.password,
                createAuthenticationInterface(),
                clientCredentials.clientId,
                clientCredentials.clientSecret,
                clientCredentials.redirectUri);
        initProviders();
        authenticationManager.signIn(null);
    }
}
