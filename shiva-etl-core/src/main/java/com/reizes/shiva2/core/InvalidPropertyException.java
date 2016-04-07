package com.reizes.shiva2.core;

public class InvalidPropertyException extends ShivaException {

	private static final long serialVersionUID = -8278733027588334126L;

	public InvalidPropertyException() {
	}

	public InvalidPropertyException(String message) {
		super(message);
	}

	public InvalidPropertyException(Throwable cause) {
		super(cause);
	}

	public InvalidPropertyException(String message, Throwable cause) {
		super(message, cause);
	}

}
