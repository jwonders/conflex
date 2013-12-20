package com.jwsphere.conflex;

public class InjectionException extends Exception {

	private static final long serialVersionUID = 1L;

	public InjectionException() {
	}
	
	public InjectionException(String message) {
		super(message);
	}
	
	public InjectionException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public InjectionException(Throwable cause) {
		super(cause);
	}
	
}
