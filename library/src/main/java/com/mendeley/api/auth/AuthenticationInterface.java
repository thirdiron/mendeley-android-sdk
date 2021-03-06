package com.mendeley.api.auth;


/**
 * This interface is implemented by MendeleySdk and is used to send callbacks
 * for successful and failed authentication.
 */
public interface AuthenticationInterface {

	public void onAuthenticated(boolean manualSignIn);
	
	public void onAuthenticationFail();

}
