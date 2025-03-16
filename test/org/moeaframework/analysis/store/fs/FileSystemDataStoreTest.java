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
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TempFiles;
import org.moeaframework.analysis.store.Blob;
import org.moeaframework.analysis.store.Container;
import org.moeaframework.analysis.store.Intent;
import org.moeaframework.analysis.store.Reference;

public class FileSystemDataStoreTest {
	
	private File tempDirectory;
	
	private FileSystemDataStore dataStore;
	
	@Before
	public void setUp() throws IOException {
		tempDirectory = TempFiles.createDirectory();
		dataStore = new FileSystemDataStore(tempDirectory);
	}
	
	@After
	public void tearDown() {
		tempDirectory = null;
		dataStore = null;
	}
	
	@Test
	public void test() {
		Reference reference = Reference.of("a", "b");
		Container container = dataStore.getContainer(reference);
		
		Assert.assertNotNull(container);
		Assert.assertFalse(container.exists());
		
		Blob blob = container.getBlob("c");
		
		Assert.assertNotNull(blob);
		Assert.assertFalse(blob.exists());
		
		blob.storeText("foo");
		
		Assert.assertTrue(container.exists());
		Assert.assertTrue(blob.exists());
		
		List<Container> containers = dataStore.listContainers();
		
		Assert.assertSize(1, containers);
		Assert.assertEquals(reference, containers.get(0).getReference());
		
		blob.delete();
		
		Assert.assertTrue(container.exists());
		Assert.assertFalse(blob.exists());
		
		blob.storeText("foo");
		
		Assert.assertTrue(container.exists());
		Assert.assertTrue(blob.exists());
		
		container.delete();
		
		Assert.assertFalse(container.exists());
		Assert.assertFalse(blob.exists());
		
		Assert.assertSize(0, dataStore.listContainers());
	}
	
	@Test
	public void testRootContainer() {
		Container container = dataStore.getRootContainer();
		
		Assert.assertNotNull(container);
		Assert.assertTrue(container.exists());
		
		Blob blob = container.getBlob("c");
		
		Assert.assertNotNull(blob);
		Assert.assertFalse(blob.exists());
		
		blob.storeText("foo");
		
		Assert.assertTrue(container.exists());
		Assert.assertTrue(blob.exists());
		
		blob.delete();
		
		Assert.assertTrue(container.exists());
		Assert.assertFalse(blob.exists());
		
		blob.storeText("foo");
		
		Assert.assertTrue(container.exists());
		Assert.assertTrue(blob.exists());
		
		container.delete();
		
		Assert.assertTrue(container.exists()); // root container always exists even when deleted
		Assert.assertFalse(blob.exists());
	}
	
	@Test
	public void testNestedContainers() {
		Reference reference1 = Reference.of("a", "b");
		
		Container container1 = dataStore.getContainer(reference1);
		container1.create();
		
		Reference reference2 = reference1.with("c", "d");
		
		Container container2 = dataStore.getContainer(reference2);
		container2.create();
		
		Assert.assertSize(2, dataStore.listContainers());
		
		Assert.assertTrue(container1.exists());
		Assert.assertTrue(container2.exists());
		
		dataStore.getRootContainer().delete();
		
		Assert.assertTrue(container1.exists());
		Assert.assertTrue(container2.exists());
		
		container1.delete();
		
		Assert.assertFalse(container1.exists());
		Assert.assertTrue(container2.exists());
		
		container2.delete();
		
		Assert.assertFalse(container1.exists());
		Assert.assertFalse(container2.exists());
		
		Assert.assertSize(0, dataStore.listContainers());
	}
	
	@Test
	public void testURI() {
		Reference reference = Reference.of("foo", "bar").with("hello", "world");
		Container container = dataStore.getContainer(reference);
		Assert.assertStringEndsWith(container.getURI().toString(), "?foo=bar&hello=world");
		
		Blob blob = container.getBlob("baz");
		Assert.assertStringEndsWith(blob.getURI().toString(), "?foo=bar&hello=world&_name=baz");
		
		Blob escapedBlob = container.getBlob("a&b=c?d#e");
		Assert.assertStringEndsWith(escapedBlob.getURI().toString(), "?foo=bar&hello=world&_name=a%2526b%253Dc%253Fd%2523e");
	}
	
	@Test
	public void testReadOnly() {
		dataStore.setIntent(Intent.READ_ONLY);
		
		Reference reference = Reference.of("a", "b");
		
		Container container = dataStore.getContainer(reference);
		Assert.assertThrows(SecurityException.class, () -> container.create());
		
		Blob blob = container.getBlob("c");
		Assert.assertThrows(SecurityException.class, () -> blob.storeText("foo"));

		Assert.assertThrows(SecurityException.class, () -> blob.delete());
		Assert.assertThrows(SecurityException.class, () -> container.delete());
		Assert.assertThrows(SecurityException.class, () -> dataStore.delete());
		
		dataStore.setIntent(Intent.READ_WRITE);
	}

}
