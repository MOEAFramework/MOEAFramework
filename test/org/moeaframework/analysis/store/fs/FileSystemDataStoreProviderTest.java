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

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.analysis.store.Blob;
import org.moeaframework.analysis.store.Container;
import org.moeaframework.analysis.store.DataStore;
import org.moeaframework.analysis.store.DataStoreException;
import org.moeaframework.analysis.store.DataStoreFactory;
import org.moeaframework.analysis.store.DataStoreURI;
import org.moeaframework.analysis.store.Reference;

public class FileSystemDataStoreProviderTest {

	@Test
	public void testGetDataStore() throws IOException {
		assertDataStore("file://temp/dataStore", Path.of("temp/dataStore"));
		assertDataStore("file:temp/dataStore", Path.of("temp/dataStore"));
		assertDataStore("temp/dataStore", Path.of("temp/dataStore"));
		
		assertDataStore("file://./temp/dataStore", Path.of("temp/dataStore"));
		assertDataStore("file:./temp/dataStore", Path.of("temp/dataStore"));
		assertDataStore("./temp/dataStore", Path.of("temp/dataStore"));
	}
	
	@Test
	public void testGetDataStoreAbsolutePaths() throws IOException {
		Assume.assumeFalse("Absolute paths fail with drive letter on Windows, skipping.", SystemUtils.IS_OS_WINDOWS);
		
		assertDataStore("file://" + Path.of("./temp/dataStore").toAbsolutePath().toString(), Path.of("temp/dataStore"));
		assertDataStore("file:" + Path.of("./temp/dataStore").toAbsolutePath().toString(), Path.of("temp/dataStore"));
		assertDataStore(Path.of("./temp/dataStore").toAbsolutePath().toString(), Path.of("temp/dataStore"));
	}
	
	@Test
	public void testGetContainer() {
		assertContainer("file://temp/dataStore?a=b", Reference.of("a", "b"));
		assertContainer("file:temp/dataStore?a=b", Reference.of("a", "b"));
		assertContainer("temp/dataStore?a=b", Reference.of("a", "b"));
		assertContainer("file://temp/dataStore", Reference.root());
		assertContainer("file:temp/dataStore", Reference.root());
		assertContainer("temp/dataStore", Reference.root());
	}
	
	@Test
	public void testGetBlob() {
		assertBlob("file://temp/dataStore?a=b#info", Reference.of("a", "b"), "info");
		assertBlob("temp/dataStore?a=b#info", Reference.of("a", "b"), "info");
		assertBlob("file://temp/dataStore#info", Reference.root(), "info");
		assertBlob("temp/dataStore#info", Reference.root(), "info");
	}
	
	@Test(expected = DataStoreException.class)
	public void testResolveBlobNoFragment() {
		Assert.assertNotNull(DataStoreFactory.getInstance().resolveBlob(URI.create("file:temp/dataStore?a=b")));
	}
	
	private void assertDataStore(String uri, Path expectedPath) throws IOException {
		DataStore dataStore = DataStoreFactory.getInstance().getDataStore(URI.create(uri));
		Assert.assertNotNull(dataStore);
		Assert.assertInstanceOf(FileSystemDataStore.class, dataStore);
		Assert.assertTrue(DataStoreURI.parse(dataStore.getURI()).getPath().endsWith(expectedPath));
	}
	
	private void assertContainer(String uri, Reference expectedReference) {
		Container container = DataStoreFactory.getInstance().resolveContainer(URI.create(uri));
		Assert.assertNotNull(container);
		Assert.assertEquals(expectedReference, container.getReference());
	}
	
	private void assertBlob(String uri, Reference expectedReference, String expectedName) {
		Blob blob = DataStoreFactory.getInstance().resolveBlob(URI.create(uri));
		Assert.assertNotNull(blob);
		Assert.assertEquals(expectedName, blob.getName());
		Assert.assertEquals(expectedReference, blob.getContainer().getReference());
	}

}
