package com.mendeley.api;

import com.mendeley.api.callbacks.profile.GetProfileCallback;
import com.mendeley.api.exceptions.MendeleyException;
import com.mendeley.api.model.Profile;

public interface BlockingSdk {
    /* PROFILES */

    /**
     * Return the user's profile information.
     */
    Profile getMyProfile() throws MendeleyException;

    /**
     * Return profile information for another user.
     *
     * @param  profileId ID of the profile to be fetched.
     */
    Profile getProfile(String profileId) throws MendeleyException;
}
