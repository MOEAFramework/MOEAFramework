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
package org.moeaframework.util.io;

import java.io.IOException;

/**
 * Similar to a {@link java.io.FileNotFoundException}, indicates that the resource was not found.
 */
public class ResourceNotFoundException extends IOException {

	private static final long serialVersionUID = -695410973076840905L;

	/**
	 * Constructs an exception indicating not resource was found with the given path.
	 * 
	 * @param owner the class attempting to load the resource
	 * @param resource the resource
	 */
	public ResourceNotFoundException(Class<?> owner, String resource) {
		super("Resource not found: " + resource + " (requestor: " + owner + ", resolved path: " +
				Resources.resolvePath(resource) + ")");
	}
	
}