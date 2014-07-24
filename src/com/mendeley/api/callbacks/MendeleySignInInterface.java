package com.mendeley.api.callbacks;

/**
 * Interface that should be implemented by the application for receiving callbacks for sign
 * in/out events.
 */
public interface MendeleySignInInterface {
	public void onSignedIn();
}
