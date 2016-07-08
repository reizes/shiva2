package com.reizes.shiva2.elasticsearch;

import java.io.IOException;

public class BulkLoadException extends IOException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4594153341809984037L;

	public BulkLoadException() {
	}

	public BulkLoadException(String message) {
		super(message);
	}

	public BulkLoadException(Throwable cause) {
		super(cause);
	}

	public BulkLoadException(String message, Throwable cause) {
		super(message, cause);
	}

}
