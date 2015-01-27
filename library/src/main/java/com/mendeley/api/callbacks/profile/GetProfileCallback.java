package com.mendeley.api.callbacks.profile;

import com.mendeley.api.exceptions.MendeleyException;
import com.mendeley.api.model.Profile;

/**
 * Interface that should be implemented by the application for receiving callbacks from profile network calls.
 */
public interface GetProfileCallback {
    public void onProfileReceived(Profile profile);
    public void onProfileNotReceived(MendeleyException mendeleyException);
}
