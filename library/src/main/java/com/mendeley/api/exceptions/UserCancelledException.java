package com.mendeley.api.exceptions;

public class UserCancelledException extends MendeleyException {
	public UserCancelledException() {
		super("Operation cancelled by the user");
	}
}
