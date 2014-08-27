package com.mendeley.api.callbacks.utils;

import com.mendeley.api.exceptions.MendeleyException;

/**
 * Interface that should be implemented by the application for receiving callbacks from profile network calls.
 */
public interface GetImageCallback {
    public void onImageReceived(byte[] byteData);
    public void onImageNotReceived(MendeleyException mendeleyException);
}
