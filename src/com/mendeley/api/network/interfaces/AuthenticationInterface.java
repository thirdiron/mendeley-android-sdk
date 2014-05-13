package com.mendeley.api.network.interfaces;

public interface AuthenticationInterface {

	public void onAuthenticated();
	
	public void onAuthenticationFail();
	
	public void onAPICallFail();
}
