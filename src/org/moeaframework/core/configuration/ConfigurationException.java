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
package org.moeaframework.core.configuration;

import org.moeaframework.core.FrameworkException;

/**
 * Indicates an error occurred when configuring an object or reading properties.
 */
public class ConfigurationException extends FrameworkException {

	private static final long serialVersionUID = -2401741284126446554L;

	/**
	 * Creates a new exception indicating an error occurred while configuring an object.
	 * 
	 * @param message a message describing what caused the error
	 * @param cause the underlying cause
	 */
	public ConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Creates a new exception indicating an error occurred while configuring an object.
	 * 
	 * @param message a message describing what caused the error
	 */
	public ConfigurationException(String message) {
		super(message);
	}

}
