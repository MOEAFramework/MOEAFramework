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

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.Settings;

/**
 * Tests the {@link MetricFileReader} class.
 */
public class MetricFileReaderTest {

	/**
	 * A valid metric file.
	 */
	public static final String COMPLETE =
			"0.0 0.1 -0.1 1.0 -1.0 1E-5\n" +
			"# commented line\n" +
			"0 10 100 1000 -10 -100\n";

	/**
	 * An incomplete metric file, missing one or more entries from a line.
	 */
	public static final String INCOMPLETE1 =
			"0.0 0.1 -0.1 1.0 -1.0 1E-5\n" +
			"-0.1 -0.2 -0.3 -0.4 -0.5\n" + // missing last entry
			"0 10 100 1000 -10 -100\n";

	/**
	 * An incomplete metric file, containing an empty line.
	 */
	public static final String INCOMPLETE2 =
			"0.0 0.1 -0.1 1.0 -1.0 1E-5\n" +
			"\n" + // empty line
			"0 10 100 1000 -10 -100\n";

	/**
	 * An incomplete metric file, containing unparseable data.
	 */
	public static final String INCOMPLETE3 =
			"0.0 0.1 -0.1 1.0 -1.0 1E-5\n" +
			"0.0 0.1 -0.1foo 1.0 -1.0 1E-5\n" + // unparseable data
			"0 10 100 1000 -10 -100\n";

	/**
	 * Performs the necessary assertions to ensure a complete metric file
	 * is read correctly.
	 * 
	 * @param reader the metric file reader
	 */
	public void validateComplete(MetricFileReader reader) {
		Assert.assertTrue(reader.hasNext());
		Assert.assertArrayEquals(new double[] { 0.0, 0.1, -0.1, 1.0, -1.0,
				1E-5 }, reader.next(), Settings.EPS);
		Assert.assertTrue(reader.hasNext());
		Assert.assertArrayEquals(new double[] { 0, 10, 100, 1000, -10, -100 }, 
				reader.next(), Settings.EPS);
		Assert.assertFalse(reader.hasNext());
		Assert.assertFalse(reader.hasNext());
	}

	/**
	 * Performs the necessary assertions to ensure an incomplete metric file
	 * is read correctly.
	 * 
	 * @param reader the metric file reader
	 */
	public void validateIncomplete(MetricFileReader reader) {
		Assert.assertTrue(reader.hasNext());
		Assert.assertArrayEquals(new double[] { 0.0, 0.1, -0.1, 1.0, -1.0,
				1E-5 }, reader.next(), Settings.EPS);
		Assert.assertFalse(reader.hasNext());
		Assert.assertFalse(reader.hasNext());
	}

	/**
	 * Tests reading COMPLETE through the {@code File} constructor.
	 * 
	 * @throws IOException if an I/O error occurred
	 */
	@Test
	public void testFileComplete() throws IOException {
		MetricFileReader reader = null;

		try {
			reader = new MetricFileReader(TestUtils.createTempFile(COMPLETE));
			validateComplete(reader);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	/**
	 * Tests reading INCOMPLETE1 through the {@code File} constructor.
	 * 
	 * @throws IOException if an I/O error occurred
	 */
	@Test
	public void testFileIncomplete1() throws IOException {
		MetricFileReader reader = null;

		try {
			reader = new MetricFileReader(TestUtils.createTempFile(
					INCOMPLETE1));
			validateIncomplete(reader);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	/**
	 * Tests reading INCOMPLETE2 through the {@code File} constructor.
	 * 
	 * @throws IOException if an I/O error occurred
	 */
	@Test
	public void testFileIncomplete2() throws IOException {
		MetricFileReader reader = null;

		try {
			reader = new MetricFileReader(TestUtils.createTempFile(
					INCOMPLETE2));
			validateIncomplete(reader);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	/**
	 * Tests reading INCOMPLETE3 through the {@code File} constructor.
	 * 
	 * @throws IOException if an I/O error occurred
	 */
	@Test
	public void testFileIncomplete3() throws IOException {
		MetricFileReader reader = null;

		try {
			reader = new MetricFileReader(TestUtils.createTempFile(
					INCOMPLETE3));
			validateIncomplete(reader);
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
		MetricFileReader reader = null;

		try {
			reader = new MetricFileReader(new StringReader(COMPLETE));
			validateComplete(reader);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	/**
	 * Tests reading INCOMPLETE1 through the {@code Reader} constructor.
	 * 
	 * @throws IOException if an I/O error occurred
	 */
	@Test
	public void testReaderIncomplete1() throws IOException {
		MetricFileReader reader = null;

		try {
			reader = new MetricFileReader(new StringReader(INCOMPLETE1));
			validateIncomplete(reader);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	/**
	 * Tests reading INCOMPLETE2 through the {@code Reader} constructor.
	 * 
	 * @throws IOException if an I/O error occurred
	 */
	@Test
	public void testReaderIncomplete2() throws IOException {
		MetricFileReader reader = null;

		try {
			reader = new MetricFileReader(new StringReader(INCOMPLETE2));
			validateIncomplete(reader);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

	/**
	 * Tests reading INCOMPLETE3 through the {@code Reader} constructor.
	 * 
	 * @throws IOException if an I/O error occurred
	 */
	@Test
	public void testReaderIncomplete3() throws IOException {
		MetricFileReader reader = null;

		try {
			reader = new MetricFileReader(new StringReader(INCOMPLETE3));
			validateIncomplete(reader);
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}

}
