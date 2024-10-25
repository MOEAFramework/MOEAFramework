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
package org.moeaframework.analysis.sample;

import java.io.IOException;
import java.util.Iterator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TempFiles;
import org.moeaframework.analysis.parameter.InvalidParameterException;
import org.moeaframework.analysis.parameter.ParameterSet;
import org.moeaframework.core.TypedProperties;

public class SamplesTest {

	public static final String PARAMETER_FILE = """
			entry1 decimal 0.0 1.0
			entry2 int 100 10000
			entry3 enum foo bar
			""";

	public static final String COMPLETE = """
			0.0 100 foo
			1.0 10000 bar
			""";

	public static final String INVALID_MISSING_ENTRY = """
			0.0 100 foo
			1.0													
			1.0 10000 bar
			""";

	public static final String INVALID_UNPARSEABLE = """
			0.0 100 foo
			1.0 10000foo 1.0
			1.0 10000 bar
			""";

	public static final String INVALID_OUT_OF_BOUNDS_1 = """
			0.0 100 foo
			1.0 99 1.0
			1.0 10000 bar
			""";

	public static final String INVALID_OUT_OF_BOUNDS_2 = """
			0.0 100 foo
			1.0 10001 1.0
			1.0 10000 bar
			""";

	private ParameterSet parameterSet;

	@Before
	public void setUp() throws IOException {
		parameterSet = ParameterSet.load(TempFiles.createFile().withContent(PARAMETER_FILE));
	}

	@After
	public void tearDown() {
		parameterSet = null;
	}

	private void validateComplete(Samples samples) {
		Iterator<Sample> it = samples.iterator();
		TypedProperties properties = null;

		Assert.assertTrue(it.hasNext());

		properties = it.next();
		Assert.assertEquals(3, properties.size());
		Assert.assertEquals("0.0", properties.getString("entry1", null));
		Assert.assertEquals("100", properties.getString("entry2", null));
		Assert.assertEquals("foo", properties.getString("entry3", null));

		Assert.assertTrue(it.hasNext());

		properties = it.next();
		Assert.assertEquals(3, properties.size());
		Assert.assertEquals("1.0", properties.getString("entry1", null));
		Assert.assertEquals("10000", properties.getString("entry2", null));
		Assert.assertEquals("bar", properties.getString("entry3", null));

		Assert.assertFalse(it.hasNext());
		Assert.assertFalse(it.hasNext());
	}

	private void validateInvalid(Samples samples) {
		Iterator<Sample> it = samples.iterator();
		
		Assert.assertTrue(it.hasNext());
		it.next();
		Assert.assertTrue(it.hasNext());
		it.next(); // should cause an exception
	}

	@Test
	public void testFileComplete() throws IOException {
		validateComplete(Samples.load(TempFiles.createFile().withContent(COMPLETE), parameterSet));
	}

	@Test(expected = IOException.class)
	public void testFileMissingEntry() throws IOException {
		validateInvalid(Samples.load(TempFiles.createFile().withContent(INVALID_MISSING_ENTRY), parameterSet));
	}

	@Test(expected = InvalidParameterException.class)
	public void testFileUnparseable() throws IOException {
		validateInvalid(Samples.load(TempFiles.createFile().withContent(INVALID_UNPARSEABLE), parameterSet));
	}

	@Test(expected = InvalidParameterException.class)
	public void testFileOutOfBounds1() throws IOException {
		validateInvalid(Samples.load(TempFiles.createFile().withContent(INVALID_OUT_OF_BOUNDS_1), parameterSet));
	}

	@Test(expected = InvalidParameterException.class)
	public void testFileOutOfBounds2() throws IOException {
		validateInvalid(Samples.load(TempFiles.createFile().withContent(INVALID_OUT_OF_BOUNDS_2), parameterSet));
	}

}
