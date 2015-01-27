package com.mendeley.api;

import android.content.Context;

import com.mendeley.api.impl.DefaultMendeleySdk;

/**
 * Convenience class used to obtain a handle to the proper MendeleySdk.
 */
public class MendeleySdkFactory {
    /**
     * Return the MendeleySdk singleton.
     */
    public static DefaultMendeleySdk getInstance() {
        return DefaultMendeleySdk.getInstance();
    }

    /**
     * Return the SDK version name.
     */
    public static String getSdkVersion(Context context) {
        return context.getResources().getString(R.string.version_name);
    }
}
