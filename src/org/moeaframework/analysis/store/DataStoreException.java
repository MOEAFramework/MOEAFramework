/* Copyright 2009-2025 David Hadka
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
package org.moeaframework.analysis.store;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.moeaframework.core.FrameworkException;

/**
 * An exception indicating a problem with a data store.  All exceptions thrown when accessing the contents of a data
 * store should extend from this type, unless a more specific type exists.
 */
public class DataStoreException extends FrameworkException {

	private static final long serialVersionUID = -8971178792964248944L;

	/**
	 * Constructs a new data store exception with the given message.
	 * 
	 * @param message the reason for the failure
	 * @param cause the underlying exception causing this failure
	 */
	public DataStoreException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new data store exception with the given message.
	 * 
	 * @param message the reason for the failure
	 */
	public DataStoreException(String message) {
		super(message);
	}
	
	/**
	 * Wraps an I/O exception into a unchecked data store exception.
	 * 
	 * @param exception the I/O exception
	 * @return the unchecked data store exception
	 */
	private static DataStoreException wrap(IOException exception) {
		return new DataStoreException("Failed with I/O exception", exception);
	}
	
	/**
	 * Wraps an I/O exception into a unchecked data store exception.
	 * 
	 * @param exception the I/O exception
	 * @param container the container that was being accessed
	 * @return the unchecked data store exception
	 */
	public static DataStoreException wrap(IOException exception, Container container) {
		return wrap(exception);
	}

	/**
	 * Wraps an I/O exception into a unchecked data store exception.
	 * 
	 * @param exception the I/O exception
	 * @param blob the blob that was being accessed
	 * @return the unchecked data store exception
	 */
	public static DataStoreException wrap(IOException exception, Blob blob) {
		if (exception instanceof FileNotFoundException) {
			return new BlobNotFoundException(blob);
		}
		
		return wrap(exception);
	}
	
}
