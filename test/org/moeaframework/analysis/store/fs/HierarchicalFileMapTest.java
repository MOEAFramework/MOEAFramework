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
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TempFiles;
import org.moeaframework.analysis.store.Reference;
import org.moeaframework.analysis.store.schema.Field;
import org.moeaframework.analysis.store.schema.Schema;

public class HierarchicalFileMapTest {
	
	@Test
	public void testSchemaless() throws IOException {
		Path path = TempFiles.createDirectory().toPath();
		
		Schema schema = Schema.schemaless();
		HierarchicalFileMap fileMap = new HierarchicalFileMap();
		
		Reference reference1 = Reference.root();
		
		Assert.assertEquals(path, fileMap.mapContainer(schema, path, reference1));
		Assert.assertEquals(path.resolve("baz"), fileMap.mapBlob(schema, path, reference1, "baz"));
		
		Reference reference2 = Reference.of("foo", "bar");
		
		Assert.assertEquals(path.resolve("foo/bar"), fileMap.mapContainer(schema, path, reference2));
		Assert.assertEquals(path.resolve("foo/bar/baz"), fileMap.mapBlob(schema, path, reference2, "baz"));
		
		Files.createDirectories(path.resolve("foo/bar"));
		Files.writeString(path.resolve("foo/bar/baz"), "foo");
		
		Reference reference3 = reference2.with("aaa", "bbb");
		
		Assert.assertEquals(path.resolve("foo/bar/aaa/bbb"), fileMap.mapContainer(schema, path, reference3));
		Assert.assertEquals(path.resolve("foo/bar/aaa/bbb/baz"), fileMap.mapBlob(schema, path, reference3, "baz"));
	}
	
	@Test
	public void testSchema() throws IOException {
		Path path = TempFiles.createDirectory().toPath();
		
		Schema schema = Schema.of(Field.named("foo").asString());
		HierarchicalFileMap fileMap = new HierarchicalFileMap();
		
		Reference reference1 = Reference.root();
		
		Assert.assertEquals(path, fileMap.mapContainer(schema, path, reference1));
		Assert.assertEquals(path.resolve("baz"), fileMap.mapBlob(schema, path, reference1, "baz"));
		
		Reference reference2 = Reference.of("foo", "bar");
		
		Assert.assertEquals(path.resolve("foo/bar"), fileMap.mapContainer(schema, path, reference2));
		Assert.assertEquals(path.resolve("foo/bar/baz"), fileMap.mapBlob(schema, path, reference2, "baz"));
		
		Files.createDirectories(path.resolve("foo/bar"));
		Files.writeString(path.resolve("foo/bar/baz"), "foo");
		
		Reference reference3 = reference2.with("aaa", "bbb");
		
		Assert.assertThrows(IllegalArgumentException.class, () -> fileMap.mapContainer(schema, path, reference3));
		Assert.assertThrows(IllegalArgumentException.class, () -> fileMap.mapBlob(schema, path, reference3, "baz"));
	}
	
	@Test
	public void testCaseSensitivity() throws IOException {
		Path path = TempFiles.createDirectory().toPath();
		
		Schema schema = Schema.schemaless();
		HierarchicalFileMap fileMap = new HierarchicalFileMap();
		
		Reference reference1 = Reference.of("foo", "bar");
		
		Path containerPath1 = fileMap.mapContainer(schema, path, reference1);
		Path blobPath1 = fileMap.mapBlob(schema, path, reference1, "baz");
		
		Files.createDirectories(containerPath1);
		Files.createFile(blobPath1);
		
		Reference reference2 = Reference.of("FOO", "BAR");
		
		Path containerPath2 = fileMap.mapContainer(schema, path, reference2);
		Path blobPath2 = fileMap.mapBlob(schema, path, reference2, "BAZ");
		
		Assert.assertEquals(containerPath1, containerPath2);
		Assert.assertEquals(blobPath1, blobPath2);
	}

}
