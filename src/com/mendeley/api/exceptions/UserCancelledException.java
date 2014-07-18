package com.mendeley.api.exceptions;

public class UserCancelledException extends MendeleyException {
    private static final long serialVersionUID = 1L;

	public UserCancelledException() {
		super("Operation cancelled by the user");
	}
}
