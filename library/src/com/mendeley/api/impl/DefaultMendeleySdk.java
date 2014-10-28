package com.mendeley.api.impl;

import android.app.Activity;

import com.mendeley.api.ClientCredentials;
import com.mendeley.api.auth.AuthenticationManager;
import com.mendeley.api.callbacks.MendeleySignInInterface;

public class DefaultMendeleySdk extends AsyncMendeleySdk {
    private static DefaultMendeleySdk instance;

    private DefaultMendeleySdk() {}

    /**
     * Return the MendeleySdk singleton.
     */
    public static DefaultMendeleySdk getInstance() {
        if (instance == null) {
            instance = new DefaultMendeleySdk();
        }
        return instance;
    }

    @Override
    public void signIn(Activity activity, MendeleySignInInterface signInCallback,
                       ClientCredentials clientCredentials) {
        this.mendeleySignInInterface = signInCallback;
        authenticationManager = new AuthenticationManager(
                activity,
                createAuthenticationInterface(),
                clientCredentials.clientId,
                clientCredentials.clientSecret,
                clientCredentials.redirectUri);
        initProviders();
        authenticationManager.signIn(activity);
    }
}
