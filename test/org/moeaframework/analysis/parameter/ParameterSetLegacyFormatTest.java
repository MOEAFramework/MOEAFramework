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
package org.moeaframework.analysis.parameter;

import java.io.IOException;
import java.io.StringReader;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TestThresholds;

public class ParameterSetLegacyFormatTest {

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

	public static final String INVALID_ENTRY = """
			entry1 0.0 1.0
			entry2 100foo 10000
			entry3 0.0 1.0
			""";

	@Test
	public void testReaderComplete() throws IOException {
		ParameterSet parameterSet = ParameterSet.load(new StringReader(COMPLETE));
		
		Assert.assertEquals(3, parameterSet.size());

		Assert.assertEquals("entry1", parameterSet.get(0).getName());
		Assert.assertInstanceOf(DecimalRange.class, parameterSet.get(0));
		Assert.assertEquals(0.0, ((DecimalRange)parameterSet.get(0)).getLowerBound(), TestThresholds.HIGH_PRECISION);
		Assert.assertEquals(1.0, ((DecimalRange)parameterSet.get(0)).getUpperBound(), TestThresholds.HIGH_PRECISION);

		Assert.assertEquals("entry2", parameterSet.get(1).getName());
		Assert.assertInstanceOf(DecimalRange.class, parameterSet.get(1));
		Assert.assertEquals(100, ((DecimalRange)parameterSet.get(1)).getLowerBound(), TestThresholds.HIGH_PRECISION);
		Assert.assertEquals(10000, ((DecimalRange)parameterSet.get(1)).getUpperBound(), TestThresholds.HIGH_PRECISION);

		Assert.assertEquals("entry3", parameterSet.get(2).getName());
		Assert.assertInstanceOf(DecimalRange.class, parameterSet.get(2));
		Assert.assertEquals(0.0, ((DecimalRange)parameterSet.get(2)).getLowerBound(), TestThresholds.HIGH_PRECISION);
		Assert.assertEquals(1.0, ((DecimalRange)parameterSet.get(2)).getUpperBound(), TestThresholds.HIGH_PRECISION);
	}

	@Test(expected = InvalidParameterException.class)
	public void testMissingEntry() throws IOException {
		ParameterSet.load(new StringReader(MISSING_ENTRY));
	}

	@Test(expected = InvalidParameterException.class)
	public void testInvalidEntry() throws IOException {
		ParameterSet.load(new StringReader(INVALID_ENTRY));
	}

}
