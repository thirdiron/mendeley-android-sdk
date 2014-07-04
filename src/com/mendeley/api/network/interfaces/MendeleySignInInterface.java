package com.mendeley.api.network.interfaces;


/**
 * Interface that is extended by all interfaces that need to be implemented by the application.
 *
 */
public interface MendeleySignInInterface extends MendeleyInterface {
	public void isSignedIn(Boolean signedIn);
}

