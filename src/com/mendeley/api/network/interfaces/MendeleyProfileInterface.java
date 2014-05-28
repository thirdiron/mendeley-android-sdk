package com.mendeley.api.network.interfaces;

import com.mendeley.api.model.Profile;
import com.mendeley.api.network.components.MendeleyResponse;

/**
 * Interface that should be implemented by the application for receiving callbacks from profile network calls.

 *
 */
public interface MendeleyProfileInterface extends MendeleyInterface {
	
	public void onProfileReceived(Profile profile, MendeleyResponse mendeleyResponse);
	
	public void onMyProfileReceived(Profile profile, MendeleyResponse mendeleyResponse);
}
