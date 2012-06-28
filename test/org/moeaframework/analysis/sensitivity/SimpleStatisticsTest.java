/* Copyright 2009-2012 David Hadka
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

import java.io.File;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.moeaframework.TestUtils;

/**
 * Tests the {@link SimpleStatistics} class.
 */
public class SimpleStatisticsTest {
	
	@Test
	public void testNormal() throws IOException {
		File input1 = TestUtils.createTempFile("0.0 0.0 0.0\n1.0 1.0 1.0");
		File input2 = TestUtils.createTempFile("0.0 0.0 0.0\n0.0 0.5 1.0\n");
		File output = TestUtils.createTempFile();
		
		SimpleStatistics.main(new String[] {
				"-m", "av",
				"-o", output.getPath(),
				input1.getPath(),
				input2.getPath()});
		
		String[] actual = new String(TestUtils.loadFile(output)).split("\\s+");
		String[] expected = new String[] { "0.0", "0.0", "0.0", "0.5", "0.75", 
				"1.0" };
		
		Assert.assertArrayEquals(expected, actual);
	}
	
	@Test(expected = IllegalArgumentException.class)
	@Ignore
	public void testMissingEntries() throws IOException {
		File input1 = TestUtils.createTempFile("0.0 0.0 0.0\n1.0 1.0 1.0");
		File input2 = TestUtils.createTempFile("0.0 0.0\n0.0 0.5 1.0\n");
		File output = TestUtils.createTempFile();
		
		SimpleStatistics.main(new String[] {
				"-m", "av",
				"-o", output.getPath(),
				input1.getPath(),
				input2.getPath()});
	}
	
	@Test(expected = IllegalArgumentException.class)
	@Ignore
	public void testEmptyFile() throws IOException {
		File input1 = TestUtils.createTempFile("0.0 0.0 0.0\n1.0 1.0 1.0");
		File input2 = TestUtils.createTempFile("");
		File output = TestUtils.createTempFile();
		
		SimpleStatistics.main(new String[] {
				"-m", "av",
				"-o", output.getPath(),
				input1.getPath(),
				input2.getPath()});
	}
	
	@Test(expected = IllegalArgumentException.class)
	@Ignore
	public void testMissingRows() throws IOException {
		File input1 = TestUtils.createTempFile("0.0 0.0 0.0\n1.0 1.0 1.0");
		File input2 = TestUtils.createTempFile("0.0 0.0 0.0\n");
		File output = TestUtils.createTempFile();
		
		SimpleStatistics.main(new String[] {
				"-m", "av",
				"-o", output.getPath(),
				input1.getPath(),
				input2.getPath()});
	}
	
	@Test(expected = IllegalArgumentException.class)
	@Ignore
	public void testNoInputs() throws IOException {
		File output = TestUtils.createTempFile();
		
		SimpleStatistics.main(new String[] {
				"-m", "av",
				"-o", output.getPath()});
	}
	
	@Test(expected = IllegalArgumentException.class)
	@Ignore
	public void testInvalidEntry() throws IOException {
		File input1 = TestUtils.createTempFile("0.0 0.0 0.0\n1.0 1.0 1.0");
		File input2 = TestUtils.createTempFile("0.0 foo 0.0\n0.0 0.5 1.0");
		File output = TestUtils.createTempFile();
		
		SimpleStatistics.main(new String[] {
				"-m", "av",
				"-o", output.getPath(),
				input1.getPath(),
				input2.getPath()});
	}
	
	@Test
	@Ignore
	public void testInfinityToNaN() throws IOException {
		File input1 = TestUtils.createTempFile("0.0 0.0 0.0\n1.0 Infinity 1.0");
		File input2 = TestUtils.createTempFile("0.0 0.0 0.0\n0.0 0.5 1.0\n");
		File output = TestUtils.createTempFile();
		
		SimpleStatistics.main(new String[] {
				"-m", "av",
				"-o", output.getPath(),
				input1.getPath(),
				input2.getPath()});
		
		String[] actual = new String(TestUtils.loadFile(output)).split("\\s+");
		String[] expected = new String[] { "0.0", "0.0", "0.0", "0.5", "0.75", 
				"1.0" };
		
		Assert.assertArrayEquals(expected, actual);
	}

}
