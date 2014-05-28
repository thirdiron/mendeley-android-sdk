package com.mendeley.api.exceptions;

/**
 * Exception class that will be added to MendeleyResponse object that is sent back to the application
 * when a response json string could not be parse to a model object.
 *
 */
public class JsonParsingException extends MendeleyException {

	private static final long serialVersionUID = 1L;

	public JsonParsingException(String message) {
		super(message);
	}

}
