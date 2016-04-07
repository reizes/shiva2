package com.reizes.shiva2.core;

public class NullExtractorException extends ShivaException {

	private static final long serialVersionUID = -4249429996091966279L;

	public NullExtractorException() {
		super();
    }

    public NullExtractorException(String message) {
		super(message);
    }

    public NullExtractorException(String message, Throwable cause) {
        super(message, cause);
    }

    public NullExtractorException(Throwable cause) {
        super(cause);
    }

}
