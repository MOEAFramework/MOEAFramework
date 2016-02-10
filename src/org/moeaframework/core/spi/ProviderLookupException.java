/* Copyright 2009-2016 David Hadka
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
package org.moeaframework.core.spi;

import org.moeaframework.core.FrameworkException;

/**
 * Exception indicating an error occurred while locating a provider.
 */
public class ProviderLookupException extends FrameworkException {

	private static final long serialVersionUID = 273130604019491242L;

	/**
	 * Constructs an exception indicating an error occurred while locating a
	 * provider.
	 * 
	 * @param message a message describing the error
	 */
	public ProviderLookupException(String message) {
		super(message);
	}
	
	/**
	 * Constructs an exception indicating an error occurred while locating a
	 * provider.
	 * 
	 * @param message a message describing the error
	 * @param cause the cause of this exception
	 */
	public ProviderLookupException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
