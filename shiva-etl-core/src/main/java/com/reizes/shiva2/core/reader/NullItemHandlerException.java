package com.reizes.shiva2.core.reader;

import com.reizes.shiva2.core.ShivaException;

public class NullItemHandlerException extends ShivaException {

	private static final long serialVersionUID = 153040562590773639L;

	public NullItemHandlerException() {
	}

	public NullItemHandlerException(String message) {
		super(message);
	}

	public NullItemHandlerException(Throwable cause) {
		super(cause);
	}

	public NullItemHandlerException(String message, Throwable cause) {
		super(message, cause);
	}

}
