package com.mendeley.api.network.interfaces;

import com.mendeley.api.exceptions.MendeleyException;
import com.mendeley.api.model.Profile;

/**
 * Interface that should be implemented by the application for receiving callbacks from profile network calls.
 */
public interface MendeleyProfileInterface extends MendeleyInterface {
	
	public void onProfileReceived(Profile profile);
	
	public void onProfileNotReceived(MendeleyException mendeleyException);

	public void onMyProfileReceived(Profile profile);

	public void onMyProfileNotReceived(MendeleyException mendeleyException);
	
}
