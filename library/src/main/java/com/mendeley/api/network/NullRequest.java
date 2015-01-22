package com.mendeley.api.network;

import com.mendeley.api.callbacks.RequestHandle;

/**
 * A placeholder for a RequestHandle.
 *
 * The main purpose of this is to provide a valid handle for requests which have already failed
 * prior to the generation of the RequestHandle. It is safe to call cancel on these.
 */
public class NullRequest implements RequestHandle {
    private static final NullRequest instance = new NullRequest();

    public static NullRequest get() {
        return instance;
    }

    private NullRequest() {}

    @Override
    public void cancel() {
    }
}
