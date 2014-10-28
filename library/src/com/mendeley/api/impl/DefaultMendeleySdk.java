package com.mendeley.api.impl;

import android.app.Activity;

import com.mendeley.api.ClientCredentials;
import com.mendeley.api.auth.AuthenticationManager;
import com.mendeley.api.callbacks.MendeleySignInInterface;
import com.mendeley.api.callbacks.document.DocumentIdList;
import com.mendeley.api.callbacks.file.FileList;
import com.mendeley.api.callbacks.folder.FolderList;
import com.mendeley.api.callbacks.group.GroupList;
import com.mendeley.api.callbacks.group.GroupMembersList;
import com.mendeley.api.exceptions.MendeleyException;
import com.mendeley.api.model.Folder;
import com.mendeley.api.model.Group;
import com.mendeley.api.params.FileRequestParameters;
import com.mendeley.api.params.FolderRequestParameters;
import com.mendeley.api.params.GroupRequestParameters;
import com.mendeley.api.params.Page;

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
