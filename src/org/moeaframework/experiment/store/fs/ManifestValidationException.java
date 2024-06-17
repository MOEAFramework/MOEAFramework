package org.moeaframework.experiment.store.fs;

import org.moeaframework.experiment.store.DataStoreException;

public class ManifestValidationException extends DataStoreException {

	private static final long serialVersionUID = 4297700379647594961L;

	public ManifestValidationException() {
		super();
	}

	public ManifestValidationException(String message, Throwable cause) {
		super(message, cause);
	}

	public ManifestValidationException(String message) {
		super(message);
	}

	public ManifestValidationException(Throwable cause) {
		super(cause);
	}

}
