package com.reizes.shiva2.etl.core.extractor;

import com.reizes.shiva2.etl.core.EtlException;

public class NullItemHandlerException extends EtlException {

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
