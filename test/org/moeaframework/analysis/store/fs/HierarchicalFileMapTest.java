/* Copyright 2009-2024 David Hadka
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
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TempFiles;
import org.moeaframework.analysis.store.Reference;
import org.moeaframework.analysis.store.schema.Field;
import org.moeaframework.analysis.store.schema.Schema;
import org.moeaframework.core.TypedProperties;

public class HierarchicalFileMapTest {
	
	@Test
	public void testSchemaless() throws IOException {
		File tempDirectory = TempFiles.createDirectory();
		Schema schema = Schema.schemaless();
		HierarchicalFileMap fileMap = new HierarchicalFileMap();
		
		TypedProperties properties1 = new TypedProperties();
		properties1.setString("foo", "bar");
		
		Reference reference1 = Reference.of(properties1);
		Path containerPath1 = fileMap.mapContainer(schema, tempDirectory.toPath(), reference1);
		Assert.assertTrue(containerPath1.endsWith(Path.of("foo/bar")));
		Assert.assertTrue(containerPath1.startsWith(tempDirectory.toPath()));
		
		Path blobPath1 = fileMap.mapBlob(schema, tempDirectory.toPath(), reference1, "baz");
		Assert.assertTrue(blobPath1.endsWith(Path.of("baz")));
		Assert.assertTrue(blobPath1.startsWith(containerPath1));
		
		Files.createDirectories(containerPath1);
		Files.writeString(blobPath1, "foo");
		
		TypedProperties properties2 = new TypedProperties();
		properties2.setString("aaa", "aaa");
		properties2.setString("foo", "bar");
		
		Reference reference2 = Reference.of(properties2);
		Path containerPath2 = fileMap.mapContainer(schema, tempDirectory.toPath(), reference2);
		Assert.assertTrue(containerPath2.endsWith(Path.of("aaa/aaa")));
		Assert.assertTrue(containerPath2.startsWith(containerPath1));
		
		Path blobPath2 = fileMap.mapBlob(schema, tempDirectory.toPath(), reference2, "baz");
		Assert.assertTrue(blobPath2.endsWith(Path.of("baz")));
		Assert.assertTrue(blobPath2.startsWith(containerPath2));
	}
	
	@Test
	public void testSchema() throws IOException {
		File tempDirectory = TempFiles.createDirectory();
		HierarchicalFileMap fileMap = new HierarchicalFileMap();
		
		Schema schema = Schema.of(Field.named("foo").asString());
		
		TypedProperties properties1 = new TypedProperties();
		properties1.setString("foo", "bar");
		
		Reference reference1 = Reference.of(properties1);
		Path containerPath1 = fileMap.mapContainer(schema, tempDirectory.toPath(), reference1);
		Assert.assertTrue(containerPath1.endsWith(Path.of("foo/bar")));
		Assert.assertTrue(containerPath1.startsWith(tempDirectory.toPath()));
		
		Path blobPath1 = fileMap.mapBlob(schema, tempDirectory.toPath(), reference1, "baz");
		Assert.assertTrue(blobPath1.endsWith(Path.of("baz")));
		Assert.assertTrue(blobPath1.startsWith(containerPath1));
		
		Files.createDirectories(containerPath1);
		Files.writeString(blobPath1, "foo");
		
		TypedProperties properties2 = new TypedProperties();
		properties2.setString("aaa", "aaa");
		properties2.setString("foo", "bar");
		
		Reference reference2 = Reference.of(properties2);
		Assert.assertThrows(IllegalArgumentException.class, () -> fileMap.mapContainer(schema, tempDirectory.toPath(), reference2));
		Assert.assertThrows(IllegalArgumentException.class, () -> fileMap.mapBlob(schema, tempDirectory.toPath(), reference2, "baz"));
	}
	
	@Test
	public void testCaseSensitivity() throws IOException {
		File tempDirectory = TempFiles.createDirectory();
		Schema schema = Schema.schemaless();
		HierarchicalFileMap fileMap = new HierarchicalFileMap();
		
		TypedProperties properties1 = new TypedProperties();
		properties1.setString("foo", "bar");
		
		Reference reference1 = Reference.of(properties1);
		Path containerPath1 = fileMap.mapContainer(schema, tempDirectory.toPath(), reference1);
		Path blobPath1 = fileMap.mapBlob(schema, tempDirectory.toPath(), reference1, "baz");
		
		Files.createDirectories(containerPath1);
		Files.createFile(blobPath1);
		
		TypedProperties properties2 = new TypedProperties();
		properties2.setString("FOO", "BAR");
		
		Reference reference2 = Reference.of(properties2);
		Path containerPath2 = fileMap.mapContainer(schema, tempDirectory.toPath(), reference2);
		Path blobPath2 = fileMap.mapBlob(schema, tempDirectory.toPath(), reference2, "BAZ");
		
		Assert.assertEquals(containerPath1, containerPath2);
		Assert.assertEquals(blobPath1, blobPath2);
	}

}
