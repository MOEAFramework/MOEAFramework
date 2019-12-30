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
package org.moeaframework.analysis.sensitivity;

import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.TestUtils;

/**
 * Tests the {@link SampleReader} class.
 */
public class SampleReaderTest {

	/**
	 * The parameter file contents.
	 */
	public static final String PARAMETER_FILE = "entry1 0.0 1.0\n"
			+ "entry2 100 10000\n" + "entry3 0.0 1.0";

	/**
	 * A valid parameter sample file.
	 */
	public static final String COMPLETE = 
			"0.0 100 0.0\n" + 
			"1.0 10000 1.0";

	/**
	 * An invalid parameter sample file, missing entries in one line.
	 */
	public static final String INVALID1 = 
			"0.0 100 0.0\n" + 
			"1.0\n" + //missing entry														
			"1.0 10000 1.0";

	/**
	 * An invalid parameter sample file, containing an empty line.
	 */
	public static final String INVALID2 = 
			"0.0 100 0.0\n" + 
			"\n" + // empty line
			"1.0 10000 1.0";

	/**
	 * An invalid parameter sample file, containing unparseable data.
	 */
	public static final String INVALID3 = "0.0 100 0.0\n" + 
			"1.0 10000foo 1.0\n" + // unparseable entry
			"1.0 10000 1.0";

	/**
	 * An invalid parameter sample file, containing out of bounds data.
	 */
	public static final String INVALID4 = 
			"0.0 100 0.0\n" + 
			"1.0 99 1.0\n" + // out of bounds entry
			"1.0 10000 1.0";

	/**
	 * An invalid parameter sample file, containing out of bounds data.
	 */
	public static final String INVALID5 = 
			"0.0 100 0.0\n" + 
			"1.0 10001 1.0\n" + // out of bounds entry
			"1.0 10000 1.0";

	/**
	 * The shared parameter file.
	 */
	private ParameterFile parameterFile;

	/**
	 * Creates the shared parameter file used for testing.
	 */
	@Before
	public void setUp() throws IOException {
		parameterFile = new ParameterFile(TestUtils
				.createTempFile(PARAMETER_FILE));
	}

	/**
	 * Removes references to shared objects so they can be garbage collected.
	 */
	@After
	public void tearDown() {
		parameterFile = null;
	}

	/**
	 * Performs the necessary assertions to ensure a complete parameter
	 * sample file is read correctly.
	 * 
	 * @param reader the parameter sample file reader
	 */
	public void validateComplete(SampleReader reader) {
		Properties properties = null;

		Assert.assertTrue(reader.hasNext());

		properties = reader.next();
		Assert.assertEquals(3, properties.size());
		Assert.assertEquals("0.0", properties.getProperty("entry1"));
		Assert.assertEquals("100.0", properties.getProperty("entry2"));
		Assert.assertEquals("0.0", properties.getProperty("entry3"));

		Assert.assertTrue(reader.hasNext());

		properties = reader.next();
		Assert.assertEquals(3, properties.size());
		Assert.assertEquals("1.0", properties.getProperty("entry1"));
		Assert.assertEquals("10000.0", properties.getProperty("entry2"));
		Assert.assertEquals("1.0", properties.getProperty("entry3"));

		Assert.assertFalse(reader.hasNext());
		Assert.assertFalse(reader.hasNext());
	}

	/**
	 * Performs the necessary assertions to ensure invalid parameter sample
	 * files are handled correctly.
	 * 
	 * @param reader the parameter sample file reader
	 */
	public void validateInvalid(SampleReader reader) {
		Assert.assertTrue(reader.hasNext());
		reader.next();
		Assert.assertTrue(reader.hasNext());
		reader.next(); // should cause an exception
	}

	/**
	 * Tests reading COMPLETE through the {@code File} constructor.
	 * 
	 * @throws IOException if an I/O error occurred
	 */
	@Test
	public void testFileComplete() throws IOException {
		SampleReader reader = null;

		try {
			reader = new SampleReader(TestUtils.createTempFile(COMPLETE),
					parameterFile);
			validateComplete(reader);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	/**
	 * Tests reading INVALID1 through the {@code File} constructor.
	 * 
	 * @throws IOException if an I/O error occurred
	 */
	@Test(expected = Exception.class)
	public void testFileInvalid1() throws IOException {
		SampleReader reader = null;

		try {
			reader = new SampleReader(TestUtils.createTempFile(INVALID1),
					parameterFile);
			validateInvalid(reader);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	/**
	 * Tests reading INVALID2 through the {@code File} constructor.
	 * 
	 * @throws IOException if an I/O error occurred
	 */
	@Test(expected = Exception.class)
	public void testFileInvalid2() throws IOException {
		SampleReader reader = null;

		try {
			reader = new SampleReader(TestUtils.createTempFile(INVALID2),
					parameterFile);
			validateInvalid(reader);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	/**
	 * Tests reading INVALID3 through the {@code File} constructor.
	 * 
	 * @throws IOException if an I/O error occurred
	 */
	@Test(expected = Exception.class)
	public void testFileInvalid3() throws IOException {
		SampleReader reader = null;

		try {
			reader = new SampleReader(TestUtils.createTempFile(INVALID3),
					parameterFile);
			validateInvalid(reader);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	/**
	 * Tests reading INVALID4 through the {@code File} constructor.
	 * 
	 * @throws IOException if an I/O error occurred
	 */
	@Test(expected = Exception.class)
	public void testFileInvalid4() throws IOException {
		SampleReader reader = null;

		try {
			reader = new SampleReader(TestUtils.createTempFile(INVALID4),
					parameterFile);
			validateInvalid(reader);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	/**
	 * Tests reading INVALID5 through the {@code File} constructor.
	 * 
	 * @throws IOException if an I/O error occurred
	 */
	@Test(expected = Exception.class)
	public void testFileInvalid5() throws IOException {
		SampleReader reader = null;

		try {
			reader = new SampleReader(TestUtils.createTempFile(INVALID5),
					parameterFile);
			validateInvalid(reader);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	/**
	 * Tests reading COMPLETE through the {@code Reader} constructor.
	 * 
	 * @throws IOException if an I/O error occurred
	 */
	@Test
	public void testReaderComplete() throws IOException {
		SampleReader reader = null;

		try {
			reader = new SampleReader(new StringReader(COMPLETE), 
					parameterFile);
			validateComplete(reader);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	/**
	 * Tests reading INVALID1 through the {@code Reader} constructor.
	 * 
	 * @throws IOException if an I/O error occurred
	 */
	@Test(expected = Exception.class)
	public void testReaderInvalid1() throws IOException {
		SampleReader reader = null;

		try {
			reader = new SampleReader(new StringReader(INVALID1), 
					parameterFile);
			validateInvalid(reader);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	/**
	 * Tests reading INVALID2 through the {@code Reader} constructor.
	 * 
	 * @throws IOException if an I/O error occurred
	 */
	@Test(expected = Exception.class)
	public void testReaderInvalid2() throws IOException {
		SampleReader reader = null;

		try {
			reader = new SampleReader(new StringReader(INVALID2), 
					parameterFile);
			validateInvalid(reader);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	/**
	 * Tests reading INVALID3 through the {@code Reader} constructor.
	 * 
	 * @throws IOException if an I/O error occurred
	 */
	@Test(expected = Exception.class)
	public void testReaderInvalid3() throws IOException {
		SampleReader reader = null;

		try {
			reader = new SampleReader(new StringReader(INVALID3), 
					parameterFile);
			validateInvalid(reader);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	/**
	 * Tests reading INVALID4 through the {@code Reader} constructor.
	 * 
	 * @throws IOException if an I/O error occurred
	 */
	@Test(expected = Exception.class)
	public void testReaderInvalid4() throws IOException {
		SampleReader reader = null;

		try {
			reader = new SampleReader(new StringReader(INVALID4), 
					parameterFile);
			validateInvalid(reader);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	/**
	 * Tests reading INVALID5 through the {@code Reader} constructor.
	 * 
	 * @throws IOException if an I/O error occurred
	 */
	@Test(expected = Exception.class)
	public void testReaderInvalid5() throws IOException {
		SampleReader reader = null;

		try {
			reader = new SampleReader(new StringReader(INVALID5), 
					parameterFile);
			validateInvalid(reader);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

}
