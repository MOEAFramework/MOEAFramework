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
package org.moeaframework.util.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.Reader;
import java.io.InputStreamReader;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.core.PRNG;
import org.moeaframework.util.io.Resources.ResourceOption;

public class ResourcesTest {

	@Test
	public void testInputStreamWithRelativePath() throws IOException {
		try (InputStream input = Resources.asStream(ResourcesTest.class, "Test.txt")) {
			Assert.assertNotNull(input);
			Assert.assertEquals("foo", read(input));
		}
	}
	
	@Test
	public void testInputStreamWithAbsolutePath() throws IOException {
		try (InputStream input = Resources.asStream(ResourcesTest.class, "/org/moeaframework/util/io/Test.txt")) {
			Assert.assertNotNull(input);
			Assert.assertEquals("foo", read(input));
		}
	}
	
	@Test
	public void testInputStreamReturnsNullIfMissing() throws IOException {
		try (InputStream input = Resources.asStream(ResourcesTest.class, "Missing.txt")) {
			Assert.assertNull(input);
		}
	}
	
	@Test(expected = ResourceNotFoundException.class)
	public void testInputStreamThrowsIfMissing() throws IOException {
		Resources.asStream(ResourcesTest.class, "Missing.txt", ResourceOption.REQUIRED);
	}
	
	@Test
	public void testInputStreamIgnoresLocalFile() throws IOException {
		File file = createLocalFile("foo");
		
		try (InputStream input = Resources.asStream(ResourcesTest.class, file.getName())) {
			Assert.assertNull(input);
		} finally {
			file.delete();
		}
	}
	
	@Test
	public void testInputStreamFindsLocalFile() throws IOException {
		File file = createLocalFile("foo");
		
		try (InputStream input = Resources.asStream(ResourcesTest.class, file.getName(), ResourceOption.FILE)) {
			Assert.assertNotNull(input);
			Assert.assertEquals("foo", read(input));
		} finally {
			file.delete();
		}
	}
	
	@Test
	public void testReaderWithRelativePath() throws IOException {
		try (Reader reader = Resources.asReader(ResourcesTest.class, "Test.txt")) {
			Assert.assertNotNull(reader);
			Assert.assertEquals("foo", read(reader));
		}
	}
	
	@Test
	public void testReaderWithAbsolutePath() throws IOException {
		try (Reader reader = Resources.asReader(ResourcesTest.class, "/org/moeaframework/util/io/Test.txt")) {
			Assert.assertNotNull(reader);
			Assert.assertEquals("foo", read(reader));
		}
	}
	
	@Test
	public void testReaderReturnsNullIfMissing() throws IOException {
		try (Reader reader = Resources.asReader(ResourcesTest.class, "Missing.txt")) {
			Assert.assertNull(reader);
		}
	}
	
	@Test(expected = ResourceNotFoundException.class)
	public void testReaderThrowsIfMissing() throws IOException {
		Resources.asReader(ResourcesTest.class, "Missing.txt", ResourceOption.REQUIRED);
	}
	
	@Test
	public void testStringWithRelativePath() throws IOException {
		String content = Resources.readString(ResourcesTest.class, "Test.txt");
		Assert.assertNotNull(content);
		Assert.assertEquals("foo", content);
	}
	
	@Test
	public void testStringWithAbsolutePath() throws IOException {
		String content = Resources.readString(ResourcesTest.class, "/org/moeaframework/util/io/Test.txt");
		Assert.assertNotNull(content);
		Assert.assertEquals("foo", content);
	}
	
	@Test
	public void testStringReturnsNullIfMissing() throws IOException {
		Assert.assertNull(Resources.readString(ResourcesTest.class, "Missing.txt"));
	}
	
	@Test(expected = ResourceNotFoundException.class)
	public void testStringThrowsIfMissing() throws IOException {
		Resources.readString(ResourcesTest.class, "Missing.txt", ResourceOption.REQUIRED);
	}
	
	@Test
	public void testFileWithRelativePath() throws IOException {
		File file = null;
	
		try {
			file = Resources.asFile(ResourcesTest.class, "Test.txt");
			
			Assert.assertNotNull(file);
			Assert.assertNull(file.getParent());
			Assert.assertEquals("foo", read(file));
		} finally {
			if (file != null) {
				file.delete();
			}
		}
	}
	
	@Test
	public void testFileWithAbsolutePath() throws IOException {
		File file = null;
		
		try {
			file = Resources.asFile(ResourcesTest.class, "/org/moeaframework/util/io/Test.txt", ResourceOption.TEMPORARY);
			Assert.assertNotNull(file);
			Assert.assertEquals(Path.of(System.getProperty("java.io.tmpdir")).normalize(), file.toPath().getParent().normalize());
			Assert.assertEquals("foo", read(file));
		} finally {
			if (file != null) {
				file.delete();
			}
		}
	}
	
	@Test(expected = ResourceNotFoundException.class)
	public void testFileThrowsIfMissing() throws IOException {
		Resources.asFile(ResourcesTest.class, "Missing.txt");
	}
	
	@Test(expected = ResourceNotFoundException.class)
	public void testFileIgnoresLocalFile() throws IOException {
		File file = createLocalFile("foo");
		
		try {
			Resources.asFile(ResourcesTest.class, file.getName());
		} finally {
			file.delete();
		}
	}
	
	@Test
	public void testFileFindsLocalFile() throws IOException {
		File file = createLocalFile("foo");
		
		try {
			File locatedFile = Resources.asFile(ResourcesTest.class, file.getName(), ResourceOption.FILE);
			Assert.assertNotNull(locatedFile);
			Assert.assertEquals(file, locatedFile);
			Assert.assertEquals("foo", read(locatedFile));
		} finally {
			file.delete();
		}
	}
	
	@Test
	public void testFileConvertsAbsoluteToRelativePath() throws IOException {
		File file = createLocalFile("foo");
		
		try {
			File locatedFile = Resources.asFile(ResourcesTest.class, "/" + file.getName(), ResourceOption.FILE);
			Assert.assertNotNull(locatedFile);
			Assert.assertEquals(file, locatedFile);
			Assert.assertEquals("foo", read(locatedFile));
		} finally {
			file.delete();
		}
	}
	
	@Test
	public void testFileSetExecutable() throws IOException {
		File file = null;
	
		try {
			file = Resources.asFile(ResourcesTest.class, "Test.txt", ResourceOption.EXECUTABLE);
			
			Assert.assertNotNull(file);
			Assert.assertTrue(file.canExecute());
		} finally {
			if (file != null) {
				file.delete();
			}
		}
	}
	
	private String read(InputStream input) throws IOException {
		if (input == null) {
			return null;
		}
		
		try (Reader reader = new InputStreamReader(input);
				StringWriter writer = new StringWriter()) {
			reader.transferTo(writer);
			return writer.toString();
		}
	}
	
	private String read(Reader reader) throws IOException {
		if (reader == null) {
			return null;
		}
		
		try (StringWriter writer = new StringWriter()) {
			reader.transferTo(writer);
			return writer.toString();
		}
	}
	
	private String read(File file) throws IOException {
		try (FileReader reader = new FileReader(file)) {
			return read(reader);
		}
	}
	
	private File createLocalFile(String content) throws IOException {
		return Files.writeString(Path.of("Test" + PRNG.nextInt() + ".txt"), "foo").toFile();
	}

}
