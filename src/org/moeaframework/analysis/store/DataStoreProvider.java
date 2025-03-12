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

import java.net.URI;

/**
 * Defines a SPI for creating data stores.  A data store is identified by a URI with a specific schema.  We recommend
 * using the components of a URI as follows:
 * <ul>
 *   <li><strong>Scheme</strong> - Identifies the type of data store
 *   <li><strong>Authority</strong> - The server containing the data store
 *   <li><strong>Path</strong> - Unique identifier for the specific data store
 *   <li><strong>Query</strong> - Key=value pairs defining the {@link Reference} to the {@link Container}
 *   <li><strong>Fragment</strong> - Name of the {@link Blob}
 * </ul>
 * <p>
 * <strong>Avoid including secrets in the URI itself.</strong>  The provided URI may appear in logs, exception
 * messages, or command line arguments, and is not a safe place to store secrets.  Instead, if required, provide a
 * reference to the secret, such as referencing an environment variable name.
 */
public abstract class DataStoreProvider {
	
	/**
	 * Constructs a data store provider.
	 */
	public DataStoreProvider() {
		super();
	}
	
	/**
	 * Resolves the data store referenced by this URI.
	 * 
	 * @param uri the URI defining the data store
	 * @return the data store, or {@code null} if not implemented by this provider
	 */
	public abstract DataStore getDataStore(URI uri);

}
