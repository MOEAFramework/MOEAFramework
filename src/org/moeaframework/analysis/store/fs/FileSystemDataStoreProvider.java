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
package org.moeaframework.analysis.store.fs;

import java.net.URI;
import java.util.ServiceConfigurationError;

import org.moeaframework.analysis.store.DataStore;
import org.moeaframework.analysis.store.DataStoreProvider;
import org.moeaframework.analysis.store.DataStoreURI;

/**
 * Provider for the {@link FileSystemDataStore}.  The URI should either begin with {@code file://} for paths relative
 * to the working directory, or {@code file:///} for absolute paths.  This is also the default if no scheme is defined.
 */
public class FileSystemDataStoreProvider extends DataStoreProvider {
		
	/**
	 * Constructs the default data store provider.
	 */
	public FileSystemDataStoreProvider() {
		super();
	}
	
	@Override
	public DataStore getDataStore(URI uri) {
		DataStoreURI dsUri = DataStoreURI.parse(uri);
		
		if (dsUri.getScheme().equalsIgnoreCase(DataStoreURI.FILE_SCHEME)) {
			try {
				return new FileSystemDataStore(dsUri.getPath());
			} catch (Exception e) {
				throw new ServiceConfigurationError("Failed to create FileSystemDataStore", e);
			}
		}
		
		return null;
	}

}
