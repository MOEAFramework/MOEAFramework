package org.moeaframework.analysis.io;

import java.io.File;

import org.moeaframework.core.FrameworkException;

/**
 * Exception indicating a result file is empty, invalid, or not properly formatted.
 */
public class EmptyResultFileException extends FrameworkException {
	
	private static final long serialVersionUID = 6731718998866888363L;

	/**
	 * Constructs a new exception indicating a result file is empty.
	 */
	public EmptyResultFileException() {
		this(null);
	}
	
	/**
	 * Constructs a new exception indicating a result file is empty.
	 * 
	 * @param file the result file
	 */
	public EmptyResultFileException(File file) {
		super("Result file " + (file == null ? "" : "'" + file + "' ") + " is empty or invalid");
	}

}
