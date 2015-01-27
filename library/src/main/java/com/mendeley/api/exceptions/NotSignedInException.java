package com.mendeley.api.exceptions;

public class NotSignedInException extends RuntimeException {
	public NotSignedInException() {
		super("User is not signed in");
	}
}
