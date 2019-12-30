/* Copyright 2009-2019 David Hadka
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
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.Settings;

/**
 * Tests the {@link FileProtection} class.
 */
public class FileProtectionTest {

	/**
	 * The file whose validation is being tested.
	 */
	private File file;

	/**
	 * Set up a protected file with the contents {@code "foo bar"}.
	 * 
	 * @throws IOException if an I/O error occurred
	 */
	@Before
	public void setUp() throws IOException {
		Settings.PROPERTIES.setString(
				Settings.KEY_FILE_PROTECTION_MODE, FileProtection.SAFE_MODE);
		
		file = TestUtils.createTempFile();

		Writer writer = FileProtection.openWriter(file);
		writer.append("foo bar");
		writer.close();
	}
	
	/**
	 * Corrupts the file.
	 * 
	 * @throws IOException if an I/O error occurred
	 */
	private void corruptFile() throws IOException {
		FileWriter writer = null;
		
		try {
			writer = new FileWriter(file);
			writer.append("foo bar!");
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}
	
	private void deleteDigestFile() throws IOException {
		FileUtils.delete(FileProtection.getDigestFile(file));
	}
	
	/**
	 * Attempts to read the file using an input stream.
	 * 
	 * @throws IOException if an I/O error occurred
	 */
	private void testInputStream() throws IOException {
		InputStream is = null;
		byte[] buffer = new byte[Settings.BUFFER_SIZE];
		
		try {
			is = FileProtection.openInputStream(file);
			
			while (is.read(buffer) != -1) {
			    //loop to read the data, which calculates the checksum
			}
		} finally {
			if (is != null) {
				is.close();
			}
		}
	}
	
	/**
	 * Attempts to read the file using a reader.
	 * 
	 * @throws IOException if an I/O error occurred
	 */
	private void testReader() throws IOException {
		Reader reader = null;
		char[] buffer = new char[Settings.BUFFER_SIZE];
		
		try {
			reader = FileProtection.openReader(file);
			
			while (reader.read(buffer) != -1) {
			    //loop to read the data, which calculates the checksum
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	/**
	 * Removes references to any shared objects for garbage collection.
	 */
	@After
	public void tearDown() {
		file = null;
	}
	
	@Test
	public void testInputStreamValid() throws IOException {
		testInputStream();
	}
	
	@Test
	public void testInputStreamNoDigestSafe() throws IOException {
		deleteDigestFile();
		testInputStream();
	}
	
	@Test(expected = ValidationException.class)
	public void testInputStreamNoDigestStrict() throws IOException {
		Settings.PROPERTIES.setString(
				Settings.KEY_FILE_PROTECTION_MODE, FileProtection.STRICT_MODE);
		
		deleteDigestFile();
		testInputStream();
	}

	@Test(expected = ValidationException.class)
	public void testInputStreamInvalid() throws IOException {
		corruptFile();
		testInputStream();
	}
	
	@Test
	public void testReaderValid() throws IOException {
		testReader();
	}
	
	@Test
	public void testReaderNoDigestSafe() throws IOException {
		deleteDigestFile();
		testReader();
	}
	
	@Test(expected = ValidationException.class)
	public void testReaderNoDigestStrict() throws IOException {
		Settings.PROPERTIES.setString(
				Settings.KEY_FILE_PROTECTION_MODE, FileProtection.STRICT_MODE);
		
		deleteDigestFile();
		testReader();
	}

	@Test(expected = ValidationException.class)
	public void testReaderInvalid() throws IOException {
		corruptFile();
		testReader();
	}
	
	@Test
	public void testValidateValid() throws IOException {
		FileProtection.validate(file);
	}
	
	@Test
	public void testValidateNoDigestSafe() throws IOException {
		deleteDigestFile();
		FileProtection.validate(file);
	}
	
	@Test(expected = ValidationException.class)
	public void testValidateNoDigestStrict() throws IOException {
		Settings.PROPERTIES.setString(
				Settings.KEY_FILE_PROTECTION_MODE, FileProtection.STRICT_MODE);
		
		deleteDigestFile();
		FileProtection.validate(file);
	}

	@Test(expected = ValidationException.class)
	public void testValidateInvalid() throws IOException {
		corruptFile();
		FileProtection.validate(file);
	}
	
	@Test
	public void testCommandLineSafe() throws Exception {
		testCommandLine("WARNING: 1 of 3 computed checksums did NOT match");
	}
	
	@Test
	public void testCommandLineStrict() throws Exception {
		Settings.PROPERTIES.setString(
				Settings.KEY_FILE_PROTECTION_MODE, FileProtection.STRICT_MODE);
		
		testCommandLine("WARNING: 2 of 3 computed checksums did NOT match");
	}
	
	/**
	 * Tests the command line utility with one file corrupted, one file with a
	 * valid digest, and one file missing the digest file.
	 * 
	 * @param expectedError the expected error message to {@code System.err}
	 * @throws Exception should not occur
	 */
	private void testCommandLine(String expectedError) throws Exception {
		File file2 = TestUtils.createTempFile();
		File file3 = TestUtils.createTempFile();
		
		//corrupt file, create valid digest for file2
		corruptFile();
		FileProtection.main(new String[] { file2.getPath() });
		
		File output = TestUtils.createTempFile();
		File error = TestUtils.createTempFile();
		
		TestUtils.pipeCommandLine(output, error, FileProtection.class, 
				"--check", file.getPath(), file2.getPath(), file3.getPath());

		Assert.assertArrayEquals((expectedError + 
				System.getProperty("line.separator")).getBytes(), 
				TestUtils.loadFile(error));
	}

}
