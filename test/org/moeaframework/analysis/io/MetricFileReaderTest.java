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

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TempFiles;
import org.moeaframework.TestThresholds;

public class MetricFileReaderTest {

	public static final String COMPLETE = """
			0.0 0.1 -0.1 1.0 -1.0 1E-5
			# commented line
			0 10 100 1000 -10 -100
			""";

	public static final String MISSING_ENTRY = """
			0.0 0.1 -0.1 1.0 -1.0 1E-5
			-0.1 -0.2 -0.3 -0.4 -0.5
			0 10 100 1000 -10 -100
			""";

	public static final String MISSING_LINE = """
			0.0 0.1 -0.1 1.0 -1.0 1E-5
			
			0 10 100 1000 -10 -100
			""";

	public static final String INVALID_ENTRY = """
			0.0 0.1 -0.1 1.0 -1.0 1E-5
			0.0 0.1 -0.1foo 1.0 -1.0 1E-5
			0 10 100 1000 -10 -100
			""";

	private void validateComplete(MetricFileReader reader) {
		Assert.assertTrue(reader.hasNext());
		Assert.assertArrayEquals(new double[] { 0.0, 0.1, -0.1, 1.0, -1.0, 1E-5 }, reader.next(), TestThresholds.HIGH_PRECISION);
		Assert.assertTrue(reader.hasNext());
		Assert.assertArrayEquals(new double[] { 0, 10, 100, 1000, -10, -100 }, reader.next(), TestThresholds.HIGH_PRECISION);
		Assert.assertFalse(reader.hasNext());
		Assert.assertFalse(reader.hasNext());
	}

	private void validateIncomplete(MetricFileReader reader) {
		Assert.assertTrue(reader.hasNext());
		Assert.assertArrayEquals(new double[] { 0.0, 0.1, -0.1, 1.0, -1.0, 1E-5 }, reader.next(), TestThresholds.HIGH_PRECISION);
		Assert.assertFalse(reader.hasNext());
		Assert.assertFalse(reader.hasNext());
	}

	@Test
	public void testFileComplete() throws IOException {
		try (MetricFileReader reader = new MetricFileReader(TempFiles.createFile().withContent(COMPLETE))) {
			validateComplete(reader);
		}
	}

	@Test
	public void testFileMissingEntry() throws IOException {
		try (MetricFileReader reader = new MetricFileReader(TempFiles.createFile().withContent(MISSING_ENTRY))) {
			validateIncomplete(reader);
		}
	}

	@Test
	public void testFileMissingLine() throws IOException {
		try (MetricFileReader reader = new MetricFileReader(TempFiles.createFile().withContent(MISSING_LINE))) {
			validateIncomplete(reader);
		}
	}

	@Test
	public void testFileInvalidEntry() throws IOException {
		try (MetricFileReader reader = new MetricFileReader(TempFiles.createFile().withContent(INVALID_ENTRY))) {
			validateIncomplete(reader);
		}
	}

	@Test
	public void testReaderComplete() throws IOException {
		try (MetricFileReader reader = new MetricFileReader(new StringReader(COMPLETE))) {
			validateComplete(reader);
		}
	}

	@Test
	public void testReaderMissingEntry() throws IOException {
		try (MetricFileReader reader = new MetricFileReader(new StringReader(MISSING_ENTRY))) {
			validateIncomplete(reader);
		}
	}

	@Test
	public void testReaderMissingLine() throws IOException {
		try (MetricFileReader reader = new MetricFileReader(new StringReader(MISSING_LINE))) {
			validateIncomplete(reader);
		}
	}

	@Test
	public void testReaderInvalidEntry() throws IOException {
		try (MetricFileReader reader = new MetricFileReader(new StringReader(INVALID_ENTRY))) {
			validateIncomplete(reader);
		}
	}

}
