package com.klc213.ats.signal;

public class DataInsufficientException extends RuntimeException {

	public DataInsufficientException(String errorMessage) {
		super(errorMessage);
	}
}
