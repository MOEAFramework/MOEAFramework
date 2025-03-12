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

import org.apache.commons.lang3.SystemUtils;
import org.junit.Assume;
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
	public void testGetDataStore() {
		assertDataStore("file://foo", Path.of("foo"));
		assertDataStore("file:foo", Path.of("foo"));
		assertDataStore("foo", Path.of("foo"));
		
		assertDataStore("file://./foo", Path.of("./foo"));
		assertDataStore("file:./foo", Path.of("./foo"));
		assertDataStore("./foo", Path.of("./foo"));
		
		assertDataStore("file://../foo", Path.of("../foo"));
		assertDataStore("file:../foo", Path.of("../foo"));
		assertDataStore("../foo", Path.of("../foo"));
		
		assertDataStore("file://foo/bar", Path.of("foo/bar"));
		assertDataStore("file:foo/bar", Path.of("foo/bar"));
		assertDataStore("foo/bar", Path.of("foo/bar"));
	}
	
	@Test
	public void testGetDataStoreAbsolutePaths() {
		Assume.assumeFalse("Absolute paths fail with drive letter on Windows, skipping.", SystemUtils.IS_OS_WINDOWS);
		
		assertDataStore("file://" + Path.of(".").toAbsolutePath().toString(), Path.of("."));
		assertDataStore("file:" + Path.of(".").toAbsolutePath().toString(), Path.of("."));
		assertDataStore(Path.of(".").toAbsolutePath().toString(), Path.of("."));
	}
	
	@Test
	public void testGetContainer() {
		assertContainer("file://foo?a=b", Reference.of("a", "b"));
		assertContainer("file:foo?a=b", Reference.of("a", "b"));
		assertContainer("foo?a=b", Reference.of("a", "b"));
		assertContainer("file://foo", Reference.root());
		assertContainer("file:foo", Reference.root());
		assertContainer("foo", Reference.root());
	}
	
	@Test
	public void testGetBlob() {
		assertBlob("file://foo?a=b#info", Reference.of("a", "b"), "info");
		assertBlob("foo?a=b#info", Reference.of("a", "b"), "info");
		assertBlob("file://foo#info", Reference.root(), "info");
		assertBlob("foo#info", Reference.root(), "info");
	}
	
	@Test(expected = DataStoreException.class)
	public void testResolveBlobNoFragment() {
		Assert.assertNotNull(DataStoreFactory.getInstance().resolveBlob(URI.create("file:foo?a=b")));
	}
	
	private void assertDataStore(String uri, Path expectedPath) {
		try {
			DataStore dataStore = DataStoreFactory.getInstance().getDataStore(URI.create(uri));
			Assert.assertNotNull(dataStore);
			Assert.assertInstanceOf(FileSystemDataStore.class, dataStore);
			Assert.assertTrue(Files.isSameFile(expectedPath, DataStoreURI.parse(dataStore.getURI()).getPath()));
		} catch (IOException e) {
			Assert.fail("Unexpected exception: " + e.getMessage());
		}
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
