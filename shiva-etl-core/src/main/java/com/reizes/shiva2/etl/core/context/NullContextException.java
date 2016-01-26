package com.reizes.shiva2.etl.core.context;

import com.reizes.shiva2.etl.core.EtlException;

public class NullContextException extends EtlException {

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
