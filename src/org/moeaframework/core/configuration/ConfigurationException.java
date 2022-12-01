package org.moeaframework.core.configuration;

import org.moeaframework.core.FrameworkException;

/**
 * Indicates an error occurred when configuring an object or reading properties.
 */
public class ConfigurationException extends FrameworkException {

	private static final long serialVersionUID = -2401741284126446554L;

	public ConfigurationException() {
		super();
	}

	public ConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConfigurationException(String message) {
		super(message);
	}

	public ConfigurationException(Throwable cause) {
		super(cause);
	}

}
