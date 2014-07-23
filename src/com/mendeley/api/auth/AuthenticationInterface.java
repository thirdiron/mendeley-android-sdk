package com.mendeley.api.auth;


/**
 * This interface is implemented by MendeleySDK and is used to send callbacks 
 * for successful and failed authentication.
 */
public interface AuthenticationInterface {

	public void onAuthenticated();
	
	public void onAuthenticationFail();

}
