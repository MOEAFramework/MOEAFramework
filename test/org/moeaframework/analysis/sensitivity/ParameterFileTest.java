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
 * Tests the {@link ParameterFile} class.
 */
public class ParameterFileTest {

	/**
	 * A valid parameter file input.
	 */
	public static final String COMPLETE = "entry1 0.0 1.0\n"
			+ "#comment 0.0 1.0\n" + "entry2 100 10000\n" + "entry3 0.0 1.0";

	/**
	 * An invalid parameter file input, where an entry is missing an element.
	 */
	public static final String INVALID1 = "entry1 0.0 1.0\n" + "entry2 100\n" + 
			"entry3 0.0 1.0";

	/**
	 * An invalid parameter file input, where an entry is missing an entire
	 * line.
	 */
	public static final String INVALID2 = "entry1 0.0 1.0\n" + "\n" + 
			"entry3 0.0 1.0";

	/**
	 * An invalid parameter file input, containing unparseable data.
	 */
	public static final String INVALID3 = "entry1 0.0 1.0\n"
			+ "entry2 100foo 10000\n" + // unparseable data
			"entry3 0.0 1.0";

	/**
	 * Performs the necessary assertions to validate a successful load of the
	 * COMPLETE input.
	 * 
	 * @param pf the parameter file
	 */
	private void validateComplete(ParameterFile pf) {
		Assert.assertEquals(3, pf.size());

		Assert.assertEquals("entry1", pf.get(0).getName());
		Assert.assertEquals(0.0, pf.get(0).getLowerBound(), Settings.EPS);
		Assert.assertEquals(1.0, pf.get(0).getUpperBound(), Settings.EPS);

		Assert.assertEquals("entry2", pf.get(1).getName());
		Assert.assertEquals(100, pf.get(1).getLowerBound(), Settings.EPS);
		Assert.assertEquals(10000, pf.get(1).getUpperBound(), Settings.EPS);

		Assert.assertEquals("entry3", pf.get(2).getName());
		Assert.assertEquals(0.0, pf.get(2).getLowerBound(), Settings.EPS);
		Assert.assertEquals(1.0, pf.get(2).getUpperBound(), Settings.EPS);
	}

	/**
	 * Tests reading COMPLETE through the {@code Reader} constructor.
	 */
	@Test
	public void testReaderComplete() throws IOException {
		validateComplete(new ParameterFile(new StringReader(COMPLETE)));
	}

	/**
	 * Tests reading INVALID1 through the {@code Reader} constructor.
	 */
	@Test(expected = IOException.class)
	public void testReaderInvalid1() throws IOException {
		new ParameterFile(new StringReader(INVALID1));
	}

	/**
	 * Tests reading INVALID2 through the {@code Reader} constructor.
	 */
	@Test(expected = IOException.class)
	public void testReaderInvalid2() throws IOException {
		new ParameterFile(new StringReader(INVALID2));
	}

	/**
	 * Tests reading INVALID3 through the {@code Reader} constructor.
	 */
	@Test(expected = NumberFormatException.class)
	public void testReaderInvalid3() throws IOException {
		new ParameterFile(new StringReader(INVALID3));
	}

	/**
	 * Tests reading COMPLETE through the {@code File} constructor.
	 */
	@Test
	public void testFileComplete() throws IOException {
		validateComplete(new ParameterFile(TestUtils.createTempFile(COMPLETE)));
	}

	/**
	 * Tests reading INVALID1 through the {@code File} constructor.
	 */
	@Test(expected = IOException.class)
	public void testFileInvalid1() throws IOException {
		new ParameterFile(TestUtils.createTempFile(INVALID1));
	}

	/**
	 * Tests reading INVALID2 through the {@code File} constructor.
	 */
	@Test(expected = IOException.class)
	public void testFileInvalid2() throws IOException {
		new ParameterFile(TestUtils.createTempFile(INVALID2));
	}

	/**
	 * Tests reading INVALID3 through the {@code File} constructor.
	 */
	@Test(expected = NumberFormatException.class)
	public void testFileInvalid3() throws IOException {
		new ParameterFile(TestUtils.createTempFile(INVALID3));
	}

}
