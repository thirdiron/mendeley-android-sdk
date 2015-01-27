package com.mendeley.integration;

public class SignInException extends Exception {
    public SignInException(String s) {
        super(s);
    }

    public SignInException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
    }
}
