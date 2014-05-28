package com.mendeley.api.exceptions;

/**
 * Exception class that will be thrown when authentication has failed.
 *
 */
public class AuthenticationException extends MendeleyException {

	private static final long serialVersionUID = 1L;

	public AuthenticationException(String message) {
		super(message);
	}

	
}
