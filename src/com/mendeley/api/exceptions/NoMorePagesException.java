package com.mendeley.api.exceptions;

/**
 * Exception class that will be added to MendeleyResponse object that is sent back to the application
 * when a paged request has already returned the final page.
 *
 */
public class NoMorePagesException extends MendeleyException {

	public NoMorePagesException() {
		super("No more pages available");
	}

	private static final long serialVersionUID = 1L;

}
