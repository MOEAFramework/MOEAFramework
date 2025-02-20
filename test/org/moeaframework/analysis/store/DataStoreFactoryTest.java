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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.analysis.store.fs.FileSystemDataStore;
import org.moeaframework.core.spi.AbstractFactoryTest;
import org.moeaframework.core.spi.ProviderNotFoundException;

public class DataStoreFactoryTest extends AbstractFactoryTest<DataStoreProvider, DataStoreFactory> {
	
	@Override
	public Class<DataStoreProvider> getProviderType() {
		return DataStoreProvider.class;
	}
	
	@Override
	public Class<DataStoreFactory> getFactoryType() {
		return DataStoreFactory.class;
	}
	
	@Test
	public void testFile() throws IOException {
		DataStoreFactory factory = createFactory();
		
		DataStore dataStore = factory.getDataStore("file://./results");
		
		Assert.assertNotNull(dataStore);
		Assert.assertInstanceOf(FileSystemDataStore.class, dataStore);
		Assert.assertTrue(Files.isSameFile(Path.of("./results"), ((FileSystemDataStore)dataStore).getRoot()));
	}
	
	@Test(expected = ProviderNotFoundException.class)
	public void testNoProvider() {
		DataStoreFactory factory = createFactory();
		factory.getDataStore("foo:///bar");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testInvalidURI() {
		DataStoreFactory factory = createFactory();
		factory.getDataStore("://bar");
	}

}
