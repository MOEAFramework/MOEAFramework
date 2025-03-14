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
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TempFiles;
import org.moeaframework.analysis.store.Reference;
import org.moeaframework.analysis.store.schema.Schema;

public class HashFileMapTest {
	
	@Test
	public void testCaseSensitivity() throws IOException {
		File tempDirectory = TempFiles.createDirectory();
		Schema schema = Schema.schemaless();
		HashFileMap fileMap = new HashFileMap(2);
		
		Reference reference1 = Reference.of("foo", "bar");
		Path blobPath1 = fileMap.mapBlob(schema, tempDirectory.toPath(), reference1, "baz");
		
		Files.createDirectories(blobPath1.getParent());
		Files.createFile(blobPath1);
		
		Reference reference2 = Reference.of("FOO", "BAR");
		Path blobPath2 = fileMap.mapBlob(schema, tempDirectory.toPath(), reference2, "BAZ");
		
		Assert.assertEquals(blobPath1, blobPath2);
	}

}
