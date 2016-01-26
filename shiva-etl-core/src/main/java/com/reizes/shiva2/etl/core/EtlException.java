package com.reizes.shiva2.etl.core;

public class EtlException extends RuntimeException {

	private static final long serialVersionUID = -3705226106250639305L;

	public EtlException() {
	}

	public EtlException(String message) {
		super(message);
	}

	public EtlException(Throwable cause) {
		super(cause);
	}

	public EtlException(String message, Throwable cause) {
		super(message, cause);
	}

}
