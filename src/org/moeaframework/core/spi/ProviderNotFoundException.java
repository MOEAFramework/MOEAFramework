/* Copyright 2009-2018 David Hadka
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

import java.text.MessageFormat;

import org.moeaframework.core.FrameworkException;

/**
 * Exception indicating a provider was not found.
 */
public class ProviderNotFoundException extends FrameworkException {

	private static final long serialVersionUID = 273130604019491242L;

	/**
	 * Constructs an exception indicating the specified provider was not found.
	 * 
	 * @param name the provider name
	 */
	public ProviderNotFoundException(String name) {
		this(name, null);
	}
	
	/**
	 * Constructs an exception indicating the specified provider was not found.
	 * 
	 * @param name the provider name
	 * @param cause the cause of this exception
	 */
	public ProviderNotFoundException(String name, Throwable cause) {
		super(MessageFormat.format("no provider for {0}", name), cause);
	}
	
}
