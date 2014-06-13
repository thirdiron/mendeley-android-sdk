package com.mendeley.api.network.interfaces;

import com.mendeley.api.exceptions.MendeleyException;
import com.mendeley.api.model.Profile;
import com.mendeley.api.network.components.Paging;

/**
 * Interface that should be implemented by the application for receiving callbacks from profile network calls.
 */
public interface MendeleyProfileInterface extends MendeleyInterface {
	
	public void onProfileReceived(Profile profile, Paging paging);
	
	public void onProfileNotReceived(MendeleyException mendeleyException);

	public void onMyProfileReceived(Profile profile, Paging paging);

	public void onMyProfileNotReceived(MendeleyException mendeleyException);
	
}
