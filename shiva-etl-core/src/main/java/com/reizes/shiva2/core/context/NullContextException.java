package com.reizes.shiva2.core.context;

import com.reizes.shiva2.core.ShivaException;

public class NullContextException extends ShivaException {

	private static final long serialVersionUID = -4381646439372545401L;

	public NullContextException() {
	}

	public NullContextException(String message) {
		super(message);
	}

	public NullContextException(Throwable cause) {
		super(cause);
	}

	public NullContextException(String message, Throwable cause) {
		super(message, cause);
	}

}
