package com.mendeley.api.exceptions;

/**
 * General exception type that is thrown whenever server returns an error code.
 * <p>
 * This is typically due to invalid parameters, but could also be an internal error.
 */
public class HttpResponseException extends MendeleyException {
    public final int httpReturnCode;

	public HttpResponseException(int httpReturnCode, String message) {
		super(message);
        this.httpReturnCode = httpReturnCode;
	}
}
