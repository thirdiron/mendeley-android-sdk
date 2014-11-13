package com.mendeley.api.exceptions;

/**
 * General exception type that is thrown whenever server returns an error code.
 * <p>
 * This is typically due to invalid parameters (httpReturnCode 4xx),
 * but could also be an internal error (httpReturnCode 5xx).
 */
public class HttpResponseException extends MendeleyException {

    public final String url;
    public final int httpReturnCode;

	public HttpResponseException(String url, int httpReturnCode, String message) {
		super(message);
        this.url = url;
        this.httpReturnCode = httpReturnCode;
	}

    @Override
    public String getMessage() {
        return String.format("%s (%s)", super.getMessage(), url);
    }
}
