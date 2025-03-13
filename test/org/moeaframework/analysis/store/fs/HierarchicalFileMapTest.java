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
		Assert.assertEquals(path.resolve("foo"), fileMap.mapBlob(schema, path, reference1, "foo"));
		
		Reference reference2 = Reference.of("a", "1");
		
		Assert.assertEquals(path.resolve("a/1"), fileMap.mapContainer(schema, path, reference2));
		Assert.assertEquals(path.resolve("a/1/foo"), fileMap.mapBlob(schema, path, reference2, "foo"));
		
		Files.createDirectories(path.resolve("a/1"));
		Files.writeString(path.resolve("a/1/foo"), "foo");
		
		Reference reference3 = reference2.with("b", "2");
		
		Assert.assertEquals(path.resolve("a/1/b/2"), fileMap.mapContainer(schema, path, reference3));
		Assert.assertEquals(path.resolve("a/1/b/2/foo"), fileMap.mapBlob(schema, path, reference3, "foo"));
	}
	
	@Test
	public void testSchema() throws IOException {
		Path path = TempFiles.createDirectory().toPath();
		
		Schema schema = Schema.of(Field.named("a").asString());
		HierarchicalFileMap fileMap = new HierarchicalFileMap();
		
		Reference reference1 = Reference.root();
		
		Assert.assertEquals(path, fileMap.mapContainer(schema, path, reference1));
		Assert.assertEquals(path.resolve("foo"), fileMap.mapBlob(schema, path, reference1, "foo"));
		
		Reference reference2 = Reference.of("a", "1");
		
		Assert.assertEquals(path.resolve("a/1"), fileMap.mapContainer(schema, path, reference2));
		Assert.assertEquals(path.resolve("a/1/foo"), fileMap.mapBlob(schema, path, reference2, "foo"));
		
		Files.createDirectories(path.resolve("a/1"));
		Files.writeString(path.resolve("a/1/foo"), "foo");
		
		Reference reference3 = reference2.with("b", "2");
		
		Assert.assertThrows(IllegalArgumentException.class, () -> fileMap.mapContainer(schema, path, reference3));
		Assert.assertThrows(IllegalArgumentException.class, () -> fileMap.mapBlob(schema, path, reference3, "foo"));
	}
	
	@Test
	public void testCaseSensitivity() throws IOException {
		Path path = TempFiles.createDirectory().toPath();
		
		Schema schema = Schema.schemaless();
		HierarchicalFileMap fileMap = new HierarchicalFileMap();
		
		Reference reference1 = Reference.of("a", "b");
		
		Path containerPath1 = fileMap.mapContainer(schema, path, reference1);
		Path blobPath1 = fileMap.mapBlob(schema, path, reference1, "foo");
		
		Files.createDirectories(containerPath1);
		Files.createFile(blobPath1);
		
		Reference reference2 = Reference.of("A", "B");
		
		Path containerPath2 = fileMap.mapContainer(schema, path, reference2);
		Path blobPath2 = fileMap.mapBlob(schema, path, reference2, "FOO");
		
		Assert.assertEquals(containerPath1, containerPath2);
		Assert.assertEquals(blobPath1, blobPath2);
	}
	
	@Test
	public void testMultipleMatchingPaths() throws IOException {
		Path path = TempFiles.createDirectory().toPath();
		
		Schema schema = Schema.schemaless();
		HierarchicalFileMap fileMap = new HierarchicalFileMap();
		
		Reference reference1 = Reference.root().with("a", 1).with("b", 2).with("c", 3);
		Reference reference2 = Reference.root().with("a", 1).with("c", 2);
		
		Files.createDirectories(fileMap.mapContainer(schema, path, reference1));
		Files.createDirectories(fileMap.mapContainer(schema, path, reference2));

		Assert.assertEquals(path.resolve("a/1/b/2/c/3"), fileMap.mapContainer(schema, path, reference1));
		Assert.assertEquals(path.resolve("a/1/c/2"), fileMap.mapContainer(schema, path, reference2));
	}

}
