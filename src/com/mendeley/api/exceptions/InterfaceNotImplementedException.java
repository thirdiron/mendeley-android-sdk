package com.mendeley.api.exceptions;

/**
 * Exception class that will be thrown when the application is calling a public SDK method
 * without implementing the appropriate interface for receiving callbacks.
 *
 */
public class InterfaceNotImplementedException extends MendeleyException{

	private static final long serialVersionUID = 1L;

	public InterfaceNotImplementedException(String message) {
		super(message);
	}
}

