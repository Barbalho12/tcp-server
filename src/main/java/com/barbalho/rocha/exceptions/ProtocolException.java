package com.barbalho.rocha.exceptions;

/**
 * Exception for breaking protocol rules
 * 
 * @author Felipe Barbalho
 *
 */
public class ProtocolException extends Exception {

	private static final long serialVersionUID = 5851368047705555541L;

	public ProtocolException(String cause, Throwable throwable) {
		super(cause, throwable);
	}

	public ProtocolException(String cause) {
		super(cause);
	}

}
