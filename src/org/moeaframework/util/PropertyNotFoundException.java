package org.moeaframework.util;

import org.moeaframework.core.FrameworkException;

/**
 * Thrown when attempting to read a property and no default value was given.
 */
public class PropertyNotFoundException extends FrameworkException {

	private static final long serialVersionUID = 2355313923356588354L;

	public PropertyNotFoundException() {
		super();
	}

	public PropertyNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public PropertyNotFoundException(String message) {
		super(message);
	}

	public PropertyNotFoundException(Throwable cause) {
		super(cause);
	}

}
