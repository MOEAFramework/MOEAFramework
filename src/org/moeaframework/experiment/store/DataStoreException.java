package org.moeaframework.experiment.store;

import org.moeaframework.experiment.ExperimentException;

public class DataStoreException extends ExperimentException {

	private static final long serialVersionUID = -8971178792964248944L;

	public DataStoreException() {
		super();
	}

	public DataStoreException(String message, Throwable cause) {
		super(message, cause);
	}

	public DataStoreException(String message) {
		super(message);
	}

	public DataStoreException(Throwable cause) {
		super(cause);
	}

}
