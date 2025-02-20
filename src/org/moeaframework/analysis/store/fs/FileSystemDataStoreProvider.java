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

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ServiceConfigurationError;

import org.moeaframework.analysis.store.DataStore;
import org.moeaframework.analysis.store.DataStoreProvider;

/**
 * Provider for the {@link FileSystemDataStore}.  The URI should either begin with {@code file://} for paths relative
 * to the working directory, or {@code file:///} for absolute paths.  This is also the default of no schema is defined.
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
		if (uri.getScheme() == null || uri.getScheme().equalsIgnoreCase("file")) {
			try {
				String path = toPath(uri);
				return new FileSystemDataStore(new File(path));
			} catch (IOException e) {
				throw new ServiceConfigurationError("Failed to create FileSystemDataStore", e);
			}
		}
		
		return null;
	}
	
	/**
	 * Returns the file path extracted from the given URI.
	 * 
	 * @param uri the URI
	 * @return the file path
	 */
	String toPath(URI uri) {
		String authority = uri.getAuthority();
		String path = uri.getPath();
		
		if (authority != null) {
			path = authority + path;
		}
		
		return path;
	}

}
