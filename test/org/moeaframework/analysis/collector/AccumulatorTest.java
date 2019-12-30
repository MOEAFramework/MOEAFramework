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
package org.moeaframework.analysis.collector;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.Settings;

public class AccumulatorTest {
	
	private Accumulator accumulator;
	
	@Before
	public void setUp() {
		accumulator = new Accumulator();
		accumulator.add("test", 5);
		accumulator.add("test", 2);
	}
	
	@After
	public void tearDown() {
		accumulator = null;
	}
	
	@Test
	public void test() {
		Assert.assertEquals(1, accumulator.keySet().size());
		Assert.assertTrue(accumulator.keySet().contains("test"));
		
		Assert.assertEquals(2, accumulator.size("test"));
		
		Assert.assertEquals(5, accumulator.get("test", 0));
		Assert.assertEquals(2, accumulator.get("test", 1));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSizeIllegalKey() {
		accumulator.size("missing");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testGetIllegalKey() {
		accumulator.get("missing", 0);
	}
	
	@Test(expected = IndexOutOfBoundsException.class)
	public void testGetInvalidIndex() {
		accumulator.get("test", 2);
	}
	
	@Test
	public void testToCSV() {
		accumulator = new Accumulator();
		accumulator.add("NFE", 100);
		accumulator.add("NFE", 200);
		accumulator.add("es,cap\"e", 0);
		accumulator.add("es,cap\"e", 1);
		
		String expected = "NFE, \"es,cap\"\"e\"" +
				Settings.NEW_LINE +
				"100, 0" +
				Settings.NEW_LINE +
				"200, 1";
		
		Assert.assertEquals(expected, accumulator.toCSV());
	}
	
	@Test
	public void testSaveCSV() throws IOException {
		accumulator = new Accumulator();
		accumulator.add("NFE", 100);
		accumulator.add("NFE", 200);
		accumulator.add("es,cap\"e", 0);
		accumulator.add("es,cap\"e", 1);
		
		String expected = "NFE, \"es,cap\"\"e\"" +
				Settings.NEW_LINE +
				"100, 0" +
				Settings.NEW_LINE +
				"200, 1";
		
		File tempFile = TestUtils.createTempFile();
		accumulator.saveCSV(tempFile);
		
		String actual = new String(TestUtils.loadFile(tempFile));
		
		Assert.assertEquals(expected, actual);
	}

}
