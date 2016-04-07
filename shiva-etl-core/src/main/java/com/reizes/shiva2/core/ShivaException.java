package com.reizes.shiva2.core;

public class ShivaException extends RuntimeException {

	private static final long serialVersionUID = -3705226106250639305L;

	public ShivaException() {
	}

	public ShivaException(String message) {
		super(message);
	}

	public ShivaException(Throwable cause) {
		super(cause);
	}

	public ShivaException(String message, Throwable cause) {
		super(message, cause);
	}

}
