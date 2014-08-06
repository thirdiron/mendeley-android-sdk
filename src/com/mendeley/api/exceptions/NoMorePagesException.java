package com.mendeley.api.exceptions;

/**
 * Exception that is thrown when a paged request has already returned the final page.
 */
public class NoMorePagesException extends MendeleyException {
	public NoMorePagesException() {
		super("No more pages available");
	}
}
