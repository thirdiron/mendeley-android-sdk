package com.mendeley.api.exceptions;

/**
 * Exception class that will be added to MendeleyResponse object that is sent back to the application
 * when the response code from the server is different than the expected one.
 *
 */
public class HttpResponseException extends MendeleyException {

	private static final long serialVersionUID = 1L;

	public HttpResponseException(String message) {
		super(message);
	}

}
