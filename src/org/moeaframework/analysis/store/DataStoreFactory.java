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
import java.util.ServiceConfigurationError;

import org.moeaframework.core.spi.AbstractFactory;
import org.moeaframework.core.spi.ProviderNotFoundException;
import org.moeaframework.util.validate.Validate;

/**
 * Factory for creating data store instances.  See {@link DataStoreProvider} for details on adding new providers.
 * <p>
 * This class is thread safe.
 */
public class DataStoreFactory extends AbstractFactory<DataStoreProvider> {
	
	/**
	 * The default data store factory.
	 */
	private static DataStoreFactory instance;
	
	static {
		instance = new DataStoreFactory();
	}
	
	/**
	 * Returns the default data store factory.
	 * 
	 * @return the default data store factory
	 */
	public static synchronized DataStoreFactory getInstance() {
		return instance;
	}

	/**
	 * Sets the default data store factory.
	 * 
	 * @param instance the default data store factory
	 */
	public static synchronized void setInstance(DataStoreFactory instance) {
		Validate.that("instance", instance).isNotNull();
		DataStoreFactory.instance = instance;
	}
	
	/**
	 * Constructs a new data store factory.
	 */
	public DataStoreFactory() {
		super(DataStoreProvider.class);
	}
	
	/**
	 * Searches through all discovered {@link DataStoreProvider} instances, returning an instance of the provider with
	 * the configured URI.  This method must throw an {@link ProviderNotFoundException} if no matching provider is
	 * found.
	 * 
	 * @param uri the URI defining the data store configuration
	 * @return an instance of the data store with the registered name
	 * @throws ProviderNotFoundException if no provider for the URI is available
	 */
	public synchronized DataStore getDataStore(URI uri) {
		for (DataStoreProvider provider : this) {
			DataStore dataStore = instantiateDataStore(provider, uri);
			
			if (dataStore != null) {
				return dataStore;
			}
		}

		throw new ProviderNotFoundException(uri.toString());
	}
	
	/**
	 * Convenience method for calling {@link #getDataStore(URI)} with a string representation of the URI.
	 * 
	 * @param str the string representation of the URI
	 * @return an instance of the data store with the registered name
	 * @throws IllegalArgumentException if the string is not a valid URI
	 * @throws ProviderNotFoundException if no provider for the URI is available
	 */
	public DataStore getDataStore(String str) {
		return getDataStore(URI.create(str));
	}
	
	/**
	 * Resolves a URI to a container.
	 * 
	 * @param uri the URI referencing a container
	 * @return the container
	 * @throws ProviderNotFoundException if no provider for the URI is available
	 */
	public Container resolveContainer(URI uri) {
		DataStoreURI dsUri = DataStoreURI.parse(uri);
		return getDataStore(uri).getContainer(Reference.of(dsUri.getQuery()));
	}
	
	/**
	 * Convenience method for calling {@link #resolveContainer(URI)} with a string representation of the URI.
	 * 
	 * @param str the string representation of the URI
	 * @return the container
	 * @throws IllegalArgumentException if the string is not a valid URI
	 * @throws ProviderNotFoundException if no provider for the URI is available
	 */
	public Container resolveContainer(String str) {
		return resolveContainer(URI.create(str));
	}
	
	/**
	 * Resolves a URI to a blob.
	 * 
	 * @param uri the URI referencing a blob
	 * @return the blob
	 * @throws DataStoreException if the URI is not a valid reference to a blob
	 * @throws ProviderNotFoundException if no provider for the URI is available
	 */
	public Blob resolveBlob(URI uri) {
		DataStoreURI dsUri = DataStoreURI.parse(uri);
		
		if (dsUri.getName() == null || dsUri.getName().isBlank()) {
			throw new DataStoreException("URI does not reference a blob, " + DataStoreURI.NAME_PARAMETER +
					" is missing or empty");
		}
		
		return getDataStore(uri).getContainer(Reference.of(dsUri.getQuery())).getBlob(dsUri.getName());
	}
	
	/**
	 * Convenience method for calling {@link #resolveBlob(URI)} with a string representation of the URI.
	 * 
	 * @param str the string representation of the URI
	 * @return the blob
	 * @throws DataStoreException if the URI is not a valid reference to a blob
	 * @throws IllegalArgumentException if the string is not a valid URI
	 * @throws ProviderNotFoundException if no provider for the URI is available
	 */
	public Blob resolveBlob(String str) {
		return resolveBlob(URI.create(str));
	}
		
	/**
	 * Attempts to instantiate the given data store using the given provider.
	 * 
	 * @param provider the data store provider
	 * @param uri the URI defining the data store configuration
	 * @return an instance of the data store; or {@code null} if the provider does not implement the data store type
	 */
	private DataStore instantiateDataStore(DataStoreProvider provider, URI uri) {
		try {
			return provider.getDataStore(uri);
		} catch (ServiceConfigurationError e) {
			System.err.println("ERROR: " + e.getMessage() +
					(e.getCause() != null ? ": " + e.getCause().getMessage() : ""));
		}
		
		return null;
	}

}
