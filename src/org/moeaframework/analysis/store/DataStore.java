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
import java.util.List;

/**
 * Interface for storing data or objects to some persistent backend.
 * <p>
 * The data store organizes related data, called {@link Blob}s, in collections called {@link Container}s.  A container
 * is referenced by a {@link Reference}, and each blob is referenced by a name.  A data store is itself an abstract
 * representation of the underlying storage, as the content could be stored on a local file system, cloud storage, or
 * a database.
 */
public interface DataStore {
	
	/**
	 * Returns the container for the given reference.  A container is always returned, though this does not imply the
	 * underlying storage exists or has been provisioned.
	 * 
	 * @param reference the data reference
	 * @return the container
	 */
	public Container getContainer(Reference reference);
	
	/**
	 * Returns a list of all containers in this data store.
	 * 
	 * @return a list of containers
	 */
	public List<Container> listContainers() throws IOException;

	/**f
	 * Returns the container for the given {@link Referenceable} object.
	 * 
	 * @param reference the data reference
	 * @return the container
	 * @see #getContainer(Reference)
	 */
	public default Container getContainer(Referenceable reference) {
		return getContainer(reference.getReference());
	}

}
