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
package org.moeaframework.algorithm.extension;

/**
 * Interface for algorithms that support extensibility.
 */
public interface Extensible {
	
	/**
	 * Returns the extensions associated with the algorithm.
	 * 
	 * @return the extensions
	 */
	public Extensions getExtensions();
	
	/**
	 * Adds the given extension and invokes its {@link Extension#onRegister} method.
	 * 
	 * @param extension the extension to add
	 */
	public default void addExtension(Extension extension) {
		getExtensions().add(extension);
	}
	
	/**
	 * Removes the given extension.
	 * 
	 * @param extension the extension to remove
	 */
	public default void removeExtension(Extension extension) {
		getExtensions().remove(extension);
	}

}
