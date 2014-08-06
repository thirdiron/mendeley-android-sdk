package com.mendeley.api.exceptions;

/**
 * General exception type that is thrown whenever server returns an error code.
 * <p>
 * This is typically due to invalid parameters (httpReturnCode 4xx),
 * but could also be an internal error (httpReturnCode 5xx).
 */
public class HttpResponseException extends MendeleyException {
    public final int httpReturnCode;

	public HttpResponseException(int httpReturnCode, String message) {
		super(message);
        this.httpReturnCode = httpReturnCode;
	}
}
