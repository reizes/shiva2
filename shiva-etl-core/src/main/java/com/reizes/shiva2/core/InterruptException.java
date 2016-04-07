package com.reizes.shiva2.core;

public class InterruptException extends ShivaException {

	private static final long serialVersionUID = -1285218221965044760L;

	public InterruptException() {
	}

	public InterruptException(String message) {
		super(message);
	}

	public InterruptException(Throwable cause) {
		super(cause);
	}

	public InterruptException(String message, Throwable cause) {
		super(message, cause);
	}

}
