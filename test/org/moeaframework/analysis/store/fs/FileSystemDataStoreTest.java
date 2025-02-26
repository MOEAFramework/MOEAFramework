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

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TempFiles;
import org.moeaframework.analysis.store.Blob;
import org.moeaframework.analysis.store.Container;
import org.moeaframework.analysis.store.Reference;
import org.moeaframework.core.TypedProperties;

public class FileSystemDataStoreTest {
	
	@Test
	public void test() throws IOException {
		File tempDirectory = TempFiles.createDirectory();
		HierarchicalFileMap fileMap = new HierarchicalFileMap();
		FileSystemDataStore dataStore = new FileSystemDataStore(tempDirectory, fileMap);
		
		TypedProperties properties1 = new TypedProperties();
		properties1.setString("foo", "bar");
		
		Reference reference = Reference.of(properties1);
		Container container = dataStore.getContainer(reference);
		
		Assert.assertFalse(container.exists());
		
		Blob blob = container.getBlob("baz");
		
		Assert.assertFalse(blob.exists());
		
		blob.storeText("foo");
		
		Assert.assertTrue(container.exists());
		Assert.assertTrue(blob.exists());
	}

}
