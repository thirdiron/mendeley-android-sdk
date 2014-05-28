package com.mendeley.api.exceptions;

/**
 * Superclass for all the mendeley exceptions that will be thrown or added 
 * to a MendeleyResponse object.
 *
 */
public class MendeleyException extends Exception {

	private static final long serialVersionUID = 1L;

	public MendeleyException(String message) {
		super(message);
	}
}
