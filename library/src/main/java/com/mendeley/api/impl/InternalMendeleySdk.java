package com.mendeley.api.impl;

import android.app.Activity;

import com.mendeley.api.ClientCredentials;
import com.mendeley.api.auth.AuthenticationManager;
import com.mendeley.api.auth.UserCredentials;
import com.mendeley.api.callbacks.MendeleySignInInterface;
import com.mendeley.api.exceptions.MendeleyException;
import com.mendeley.api.model.File;

import java.io.InputStream;

/**
 * Internal version of MendeleySdk.
 * <p>
 * This is used to run integration tests on the SDK, in which sign is handled via the resource owner
 * password flow.
 * <p>
 * Developer applications should not use this class.
 */
public class InternalMendeleySdk extends AsyncMendeleySdk {
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

    @Override
    public void signIn(Activity activity, MendeleySignInInterface signInCallback, ClientCredentials clientCredentials) {
        throw new UnsupportedOperationException("Internal SDK only supports resource owner password auth flow");
    }
}
