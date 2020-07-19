package com.barbalho.rocha.exceptions;

/**
 * Exception for data access layer errors
 * 
 * @author Felipe Barbalho
 *
 */
public class DaoException extends Exception {

	private static final long serialVersionUID = 5851368047705555541L;

	public DaoException(String cause, Throwable throwable) {
		super(cause, throwable);
	}

}
