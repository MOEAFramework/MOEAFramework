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
 * Interface for storing data or objects to some persistent backend.
 * <p>
 * The data store organizes related data, called {@link Blob}s, in collections called {@link Container}s.  A container
 * is referenced by a {@link Key}, and each blob is referenced by a name.
 */
public interface DataStore {
	
	public Container getContainer(Key key);

	public default boolean contains(Key key) throws IOException {
		return getContainer(key).exists();
	}
	
	public default Container getContainer(Keyed keyed) {
		return getContainer(keyed.getKey());
	}
	
	public default boolean contains(Keyed keyed) throws IOException {
		return contains(keyed.getKey());
	}

}
