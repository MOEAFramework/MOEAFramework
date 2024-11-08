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
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TempFiles;

public class FileMapTest {
	
	@Test
	public void testASCIIFilenames() throws IOException {
		List<Integer> chars = Stream.concat(IntStream.range(32, 128).boxed(), IntStream.of(9, 10, 13).boxed()).toList();
		File tempDirectory = TempFiles.createDirectory();
		
		for (int charDec : chars) {
			String filename = "" + (char)charDec;

			try {
				Path escapedFilename = FileMap.escapePath(filename);
				Files.writeString(new File(tempDirectory, escapedFilename.toString()).toPath(), "test");
			} catch (IOException e) {
				Assert.fail("File '" + filename + "' was not escaped propertly: " + e.getMessage());
			}
		}
	}
	
	@Test
	public void testPrefix() throws IOException {
		Assert.assertEquals("%46", FileMap.escapePath(".").toString());
		Assert.assertEquals("%46.", FileMap.escapePath("..").toString());
		Assert.assertEquals("%7E", FileMap.escapePath("~").toString());
		Assert.assertEquals("%46foo", FileMap.escapePath(".foo").toString());
		Assert.assertEquals("%46.foo", FileMap.escapePath("..foo").toString());
		Assert.assertEquals("%7Efoo", FileMap.escapePath("~foo").toString());
	}

}
