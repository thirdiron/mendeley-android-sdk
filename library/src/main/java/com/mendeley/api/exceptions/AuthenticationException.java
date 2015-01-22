package com.mendeley.api.exceptions;

/**
 * Exception that is thrown when authentication has failed.
 */
public class AuthenticationException extends MendeleyException {
	public AuthenticationException(String message) {
		super(message);
	}
}
