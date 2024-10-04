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

public class ParameterFileTest {

	public static final String COMPLETE = """
			entry1 0.0 1.0
			#comment 0.0 1.0
			entry2 100 10000
			entry3 0.0 1.0
			""";

	public static final String MISSING_ENTRY = """
			entry1 0.0 1.0
			entry2 100
			entry3 0.0 1.0
			""";

	public static final String MISSING_LINE = """
			entry1 0.0 1.0
			
			entry3 0.0 1.0
			""";

	public static final String INVALID_ENTRY = """
			entry1 0.0 1.0
			entry2 100foo 10000
			entry3 0.0 1.0
			""";

	private void validateComplete(ParameterFile pf) {
		Assert.assertEquals(3, pf.size());

		Assert.assertEquals("entry1", pf.get(0).getName());
		Assert.assertEquals(0.0, pf.get(0).getLowerBound(), TestThresholds.HIGH_PRECISION);
		Assert.assertEquals(1.0, pf.get(0).getUpperBound(), TestThresholds.HIGH_PRECISION);

		Assert.assertEquals("entry2", pf.get(1).getName());
		Assert.assertEquals(100, pf.get(1).getLowerBound(), TestThresholds.HIGH_PRECISION);
		Assert.assertEquals(10000, pf.get(1).getUpperBound(), TestThresholds.HIGH_PRECISION);

		Assert.assertEquals("entry3", pf.get(2).getName());
		Assert.assertEquals(0.0, pf.get(2).getLowerBound(), TestThresholds.HIGH_PRECISION);
		Assert.assertEquals(1.0, pf.get(2).getUpperBound(), TestThresholds.HIGH_PRECISION);
	}

	@Test
	public void testReaderComplete() throws IOException {
		validateComplete(new ParameterFile(new StringReader(COMPLETE)));
	}

	@Test(expected = IOException.class)
	public void testReaderMissingEntry() throws IOException {
		new ParameterFile(new StringReader(MISSING_ENTRY));
	}

	@Test(expected = IOException.class)
	public void testReaderMissingLine() throws IOException {
		new ParameterFile(new StringReader(MISSING_LINE));
	}

	@Test(expected = NumberFormatException.class)
	public void testReaderInvalidEntry() throws IOException {
		new ParameterFile(new StringReader(INVALID_ENTRY));
	}

	@Test
	public void testFileComplete() throws IOException {
		validateComplete(new ParameterFile(TempFiles.createFile().withContent(COMPLETE)));
	}

	@Test(expected = IOException.class)
	public void testFileMissingEntry() throws IOException {
		new ParameterFile(TempFiles.createFile().withContent(MISSING_ENTRY));
	}

	@Test(expected = IOException.class)
	public void testFileMissingLine() throws IOException {
		new ParameterFile(TempFiles.createFile().withContent(MISSING_LINE));
	}

	@Test(expected = NumberFormatException.class)
	public void testFileInvalidEntry() throws IOException {
		new ParameterFile(TempFiles.createFile().withContent(INVALID_ENTRY));
	}

}
