package com.mendeley.api.network.interfaces;

/**
 * Interface that should be implemented by the application for receiving callbacks for sign
 * in/out events.
 */
public interface MendeleySignInInterface extends MendeleyInterface {
	public void isSignedIn(Boolean signedIn);
}
