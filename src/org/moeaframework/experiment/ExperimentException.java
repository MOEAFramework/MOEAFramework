package org.moeaframework.experiment;

import org.moeaframework.core.FrameworkException;

public class ExperimentException extends FrameworkException {

	private static final long serialVersionUID = 3874580203283751062L;

	public ExperimentException() {
		super();
	}

	public ExperimentException(String message, Throwable cause) {
		super(message, cause);
	}

	public ExperimentException(String message) {
		super(message);
	}

	public ExperimentException(Throwable cause) {
		super(cause);
	}

}
