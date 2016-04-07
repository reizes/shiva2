package com.reizes.shiva2.core;

public class NullArgumentException extends ShivaException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4492806954916272214L;

	public NullArgumentException() {
		super();
    }

    public NullArgumentException(String message) {
		super(message);
    }

    public NullArgumentException(String message, Throwable cause) {
        super(message, cause);
    }

    public NullArgumentException(Throwable cause) {
        super(cause);
    }

}
