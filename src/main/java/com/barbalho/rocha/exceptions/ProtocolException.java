package com.barbalho.rocha.exceptions;

public class ProtocolException extends Exception {

    private static final long serialVersionUID = 5851368047705555541L;

    public ProtocolException(String cause, Throwable throwable) {
        super(cause, throwable);
    }

    public ProtocolException(String cause) {
        super(cause);
    }

}
