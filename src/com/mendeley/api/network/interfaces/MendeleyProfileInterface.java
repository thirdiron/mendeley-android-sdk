package com.mendeley.api.network.interfaces;

import com.mendeley.api.exceptions.MendeleyException;
import com.mendeley.api.model.Profile;

public interface MendeleyProfileInterface extends MendeleyInterface {
	
	public void onProfileReceived(Profile profile, MendeleyException exception);
	
	public void onMyProfileReceived(Profile profile, MendeleyException exception);
}
