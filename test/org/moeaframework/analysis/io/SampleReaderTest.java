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
package org.moeaframework.analysis.io;

import java.io.IOException;
import java.io.StringReader;
import java.util.NoSuchElementException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TempFiles;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.util.TypedProperties;

public class SampleReaderTest {

	public static final String PARAMETER_FILE = """
			entry1 0.0 1.0
			entry2 100 10000
			entry3 0.0 1.0
			""";

	public static final String COMPLETE = """
			0.0 100 0.0
			1.0 10000 1.0
			""";

	public static final String INVALID_MISSING_ENTRY = """
			0.0 100 0.0
			1.0													
			1.0 10000 1.0
			""";

	public static final String INVALID_EMPTY_LINE = """
			0.0 100 0.0
			
			1.0 10000 1.0
			""";

	public static final String INVALID_UNPARSEABLE = """
			0.0 100 0.0
			1.0 10000foo 1.0
			1.0 10000 1.0
			""";

	public static final String INVALID_OUT_OF_BOUNDS_1 = """
			0.0 100 0.0
			1.0 99 1.0
			1.0 10000 1.0
			""";

	public static final String INVALID_OUT_OF_BOUNDS_2 = """
			0.0 100 0.0
			1.0 10001 1.0
			1.0 10000 1.0
			""";

	private ParameterFile parameterFile;

	@Before
	public void setUp() throws IOException {
		parameterFile = new ParameterFile(TempFiles.createFileWithContent(PARAMETER_FILE));
	}

	@After
	public void tearDown() {
		parameterFile = null;
	}

	private void validateComplete(SampleReader reader) {
		TypedProperties properties = null;

		Assert.assertTrue(reader.hasNext());

		properties = reader.next();
		Assert.assertEquals(3, properties.size());
		Assert.assertEquals("0.0", properties.getString("entry1", null));
		Assert.assertEquals("100.0", properties.getString("entry2", null));
		Assert.assertEquals("0.0", properties.getString("entry3", null));

		Assert.assertTrue(reader.hasNext());

		properties = reader.next();
		Assert.assertEquals(3, properties.size());
		Assert.assertEquals("1.0", properties.getString("entry1", null));
		Assert.assertEquals("10000.0", properties.getString("entry2", null));
		Assert.assertEquals("1.0", properties.getString("entry3", null));

		Assert.assertFalse(reader.hasNext());
		Assert.assertFalse(reader.hasNext());
	}

	private void validateInvalid(SampleReader reader) {
		Assert.assertTrue(reader.hasNext());
		reader.next();
		Assert.assertTrue(reader.hasNext());
		reader.next(); // should cause an exception
	}

	@Test
	public void testFileComplete() throws IOException {
		try (SampleReader reader = new SampleReader(TempFiles.createFileWithContent(COMPLETE), parameterFile)) {
			validateComplete(reader);
		}
	}

	@Test(expected = FrameworkException.class)
	public void testFileMissingEntry() throws IOException {
		try (SampleReader reader = new SampleReader(TempFiles.createFileWithContent(INVALID_MISSING_ENTRY), parameterFile)) {
			validateInvalid(reader);
		}
	}

	@Test(expected = FrameworkException.class)
	public void testFileEmptyLine() throws IOException {
		try (SampleReader reader = new SampleReader(TempFiles.createFileWithContent(INVALID_EMPTY_LINE), parameterFile)) {
			validateInvalid(reader);
		}
	}

	@Test(expected = FrameworkException.class)
	public void testFileUnparseable() throws IOException {
		try (SampleReader reader = new SampleReader(TempFiles.createFileWithContent(INVALID_UNPARSEABLE), parameterFile)) {
			validateInvalid(reader);
		}
	}

	@Test(expected = FrameworkException.class)
	public void testFileOutOfBounds1() throws IOException {
		try (SampleReader reader = new SampleReader(TempFiles.createFileWithContent(INVALID_OUT_OF_BOUNDS_1), parameterFile)) {
			validateInvalid(reader);
		}
	}

	@Test(expected = FrameworkException.class)
	public void testFileOutOfBounds2() throws IOException {
		try (SampleReader reader = new SampleReader(TempFiles.createFileWithContent(INVALID_OUT_OF_BOUNDS_2), parameterFile)) {
			validateInvalid(reader);
		}
	}

	@Test
	public void testReaderComplete() throws IOException {
		try (SampleReader reader = new SampleReader(new StringReader(COMPLETE), parameterFile)) {
			validateComplete(reader);
		}
	}

	@Test(expected = FrameworkException.class)
	public void testReaderMissingEntry() throws IOException {
		try (SampleReader reader = new SampleReader(new StringReader(INVALID_MISSING_ENTRY), parameterFile)) {
			validateInvalid(reader);
		}
	}

	@Test(expected = FrameworkException.class)
	public void testReaderEmptyLine() throws IOException {
		try (SampleReader reader = new SampleReader(new StringReader(INVALID_EMPTY_LINE), parameterFile)) {
			validateInvalid(reader);
		}
	}

	@Test(expected = FrameworkException.class)
	public void testReaderUnparseable() throws IOException {
		try (SampleReader reader = new SampleReader(new StringReader(INVALID_UNPARSEABLE), parameterFile)) {
			validateInvalid(reader);
		}
	}

	@Test(expected = FrameworkException.class)
	public void testReaderOutOfBounds1() throws IOException {
		try (SampleReader reader = new SampleReader(new StringReader(INVALID_OUT_OF_BOUNDS_1), parameterFile)) {
			validateInvalid(reader);
		}
	}

	@Test(expected = FrameworkException.class)
	public void testReaderOutOfBounds2() throws IOException {
		try (SampleReader reader = new SampleReader(new StringReader(INVALID_OUT_OF_BOUNDS_2), parameterFile)) {
			validateInvalid(reader);
		}
	}
	
	@Test(expected = NoSuchElementException.class)
	public void testNextAfterEndOfFile() throws IOException {
		try (SampleReader reader = new SampleReader(TempFiles.createFileWithContent(COMPLETE), parameterFile)) {
			validateComplete(reader);
			reader.next();
		}
	}

}
