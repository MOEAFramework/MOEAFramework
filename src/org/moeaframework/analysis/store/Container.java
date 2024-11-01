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
package org.moeaframework.analysis.store;

import java.io.IOException;

/**
 * A container of blobs.
 */
public interface Container {
	
	/**
	 * Gets the key for this container.
	 * 
	 * @return the key
	 */
	Key getKey();
	
	/**
	 * Gets a reference to a blob with the given name.
	 * 
	 * @param name the blob name
	 * @return the blob reference
	 */
	Blob getBlob(String name);
	
	/**
	 * Creates the underlying, physical container.  It is typically not required to create the container explicitly,
	 * as writing to a blob will automatically create the container, if required.
	 * <p>
	 * Note that implementations may not have a physical representation of a container, such that this method has no
	 * effect.  Avoid depending on this method for any particular purpose.
	 * 
	 * @throws IOException if an I/O error occurred
	 */
	void create() throws IOException;
	
	/**
	 * Returns {@code true} if the underlying, physical container exists.
	 * <p>
	 * Note that implementations may not have a physical representation of a container, in which case this method
	 * will always return {@code true}.  Avoid depending on this method for checking if data exists; instead use
	 * {@link #contains(String)} to check if specific blobs exist.
	 * 
	 * @return {@code true} if the container exists; {@code false} otherwise
	 * @throws IOException if an I/O error occurred
	 */
	boolean exists() throws IOException;
	
	/**
	 * Returns {@code true} if the blob identified by this name exists within this container.
	 * 
	 * @param name the blob name
	 * @return {@code true} if the blob exists; {@code false} otherwise
	 * @throws IOException if an I/O error occurred
	 */
	public default boolean contains(String name) throws IOException {
		return getBlob(name).exists();
	}

}
