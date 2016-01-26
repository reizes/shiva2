package com.reizes.shiva2.etl.core;

public class EtlInterruptException extends EtlException {

	private static final long serialVersionUID = -1285218221965044760L;

	public EtlInterruptException() {
	}

	public EtlInterruptException(String message) {
		super(message);
	}

	public EtlInterruptException(Throwable cause) {
		super(cause);
	}

	public EtlInterruptException(String message, Throwable cause) {
		super(message, cause);
	}

}
