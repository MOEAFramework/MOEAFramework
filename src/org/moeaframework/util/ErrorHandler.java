/* Copyright 2009-2024 David Hadka
 *
 * This file is part of the MOEA Framework.
 *
 * The MOEA Framework is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * The MOEA Framework is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the MOEA Framework.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.moeaframework.util;

import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

import org.moeaframework.core.FrameworkException;

/**
 * Helper class for managing warnings and errors.
 */
public class ErrorHandler {
	
	private PrintStream messageStream;
	
	private boolean errorsAreFatal;
	
	private boolean warningsAreFatal;
	
	private boolean suppressDuplicates;
	
	private boolean displayFullStackTrace;
	
	private boolean isError;
	
	private final Set<String> emittedMessages;
	
	/**
	 * Constructs a new error handler instance.
	 */
	public ErrorHandler() {
		super();
		messageStream = System.err;
		emittedMessages = new HashSet<>();
	}
	
	/**
	 * Returns {@code true} if an error occurred but was suppressed; {@code false} otherwise.
	 * 
	 * @return if an error occurred
	 */
	public boolean isError() {
		return isError;
	}
	
	/**
	 * Clears the state of this error handler.
	 */
	public void reset() {
		isError = false;
		emittedMessages.clear();
	}
	
	/**
	 * Sets the stream where warnings and errors are logged.  Note that this stream is not managed by this class and
	 * will not be closed.
	 * 
	 * @param messageStream the new stream
	 */
	public void setMessageStream(PrintStream messageStream) {
		this.messageStream = messageStream;
	}
	
	/**
	 * Controls if duplicate logged warnings and errors are displayed or suppressed.  If {@code true}, each unique
	 * warning or error is logged once.
	 * 
	 * @param suppressDuplicates the new duplicate message behavior
	 */
	public void setSuppressDuplicates(boolean suppressDuplicates) {
		this.suppressDuplicates = suppressDuplicates;
	}
	
	/**
	 * Controls how errors are handled.  If {@code false}, any errors are logged but suppressed.  If {@code true}, an
	 * exception is thrown.
	 * 
	 * @param errorsAreFatal the new error handling behavior
	 */
	public void setErrorsAreFatal(boolean errorsAreFatal) {
		this.errorsAreFatal = errorsAreFatal;
	}
	
	/**
	 * Controls how warnings are handled.  If {@code false}, any warnings are logged but suppressed.  If {@code true},
	 * an exception is thrown.
	 * 
	 * @param warningsAreFatal the new setting
	 */
	public void setWarningsAreFatal(boolean warningsAreFatal) {
		this.warningsAreFatal = warningsAreFatal;
	}
	
	/**
	 * Controls how exceptions are formatted.  If {@code true}, the full stack trace is displayed.  If {@code false},
	 * only the message is displayed.
	 * 
	 * @param displayFullStackTrace the new setting
	 */
	public void setDisplayFullStackTrace(boolean displayFullStackTrace) {
		this.displayFullStackTrace = displayFullStackTrace;
	}
	
	/**
	 * Called when a warning condition is encountered.
	 * 
	 * @param message the warning message
	 * @throws FrameworkException if {@code warningsAreFatal} is {@code true}
	 */
	public void warn(String message) {
		if (warningsAreFatal) {
			throw new FrameworkException(message);
		}
		
		if (!suppressDuplicates || !emittedMessages.contains(message)) {
			messageStream.println(message);
		}
		
		emittedMessages.add(message);
	}
	
	/**
	 * Called when a warning condition is encountered.
	 * 
	 * @param pattern the warning message pattern used by {@link MessageFormat}
	 * @param arguments the arguments used to format the message
	 * @throws FrameworkException if {@code warningsAreFatal} is {@code true}
	 */
	public void warn(String pattern, Object... arguments) {
		warn(MessageFormat.format(pattern, arguments));
	}
	
	/**
	 * Called when an error condition is encountered.
	 * 
	 * @param message the error message
	 * @throws FrameworkException if {@code errorsAreFatal} is {@code true}
	 */
	public void error(String message) {
		if (errorsAreFatal) {
			throw new FrameworkException(message);
		}
		
		if (!suppressDuplicates || !emittedMessages.contains(message)) {
			messageStream.println(message);
		}
		
		isError = true;
		emittedMessages.add(message);
	}
	
	/**
	 * Called when an error condition is encountered.
	 * 
	 * @param pattern the error message pattern used by {@link MessageFormat}
	 * @param arguments the arguments used to format the message
	 * @throws FrameworkException if {@code errorsAreFatal} is {@code true}
	 */
	public void error(String pattern, Object... arguments) {
		error(MessageFormat.format(pattern, arguments));
	}
	
	/**
	 * Called when an exception occurs.
	 * 
	 * @param throwable the exception
	 * @throws FrameworkException if {@code errorsAreFatal} is {@code true}
	 */
	public void error(Throwable throwable) {
		if (displayFullStackTrace) {
			error(throwable.getStackTrace().toString());
		} else {
			error(throwable.getMessage());
		}
	}

}
