package com.mendeley.api.impl;

import android.app.Activity;

import com.mendeley.api.ClientCredentials;
import com.mendeley.api.auth.AuthenticationManager;
import com.mendeley.api.callbacks.MendeleySignInInterface;

public class DefaultMendeleySdk extends BaseMendeleySdk {
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

    /**
     * Sign the user in.
     *
     * @param activity used for creating the sign-in activity.
     * @param clientCredentials your app's Mendeley ID/secret/Uri, from the registration process.
     * @param signInCallback used to receive sign in/out events. May be null.
     */
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
