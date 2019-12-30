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

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.FrameworkException;

/**
 * Tests the {@link ResultFileEvaluator} class.
 */
public class ResultFileEvaluatorTest {
	
	public static final String COMPLETE = 
			"# Problem = DTLZ2_2\n" +
			"# Variables = 0\n" +
			"# Objectives = 2\n" + 
			"0.0 1.0\n" + 
			"1.0 0.0\n" + 
			"#\n" + 
			"0.0 1.0\n" + 
			"1.0 0.0\n" +
			"#\n";
	
	public static final String EMPTY = 
			"# Problem = DTLZ2_2\n" +
			"# Variables = 0\n" +
			"# Objectives = 2\n";
	
	@Test
	public void testComplete() throws Exception {
		File input = TestUtils.createTempFile(COMPLETE);
		File output = TestUtils.createTempFile();
		
		ResultFileEvaluator.main(new String[] {
			"--problem", "DTLZ2_2",
			"--reference", "pf/DTLZ2.2D.pf",
			"--input", input.getAbsolutePath(),
			"--output", output.getAbsolutePath()});
		
		MetricFileReader reader = null;
		
		try {
			reader = new MetricFileReader(output);
			
			Assert.assertTrue(reader.hasNext());
			Assert.assertNotNull(reader.next());
			Assert.assertTrue(reader.hasNext());
			Assert.assertNotNull(reader.next());
			Assert.assertFalse(reader.hasNext());
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}
	
	@Test
	public void testEmpty() throws Exception {
		File input = TestUtils.createTempFile(EMPTY);
		File output = TestUtils.createTempFile();
		
		ResultFileEvaluator.main(new String[] {
			"--problem", "DTLZ2_2",
			"--reference", "pf/DTLZ2.2D.pf",
			"--input", input.getAbsolutePath(),
			"--output", output.getAbsolutePath()});
		
		MetricFileReader reader = null;
		
		try {
			reader = new MetricFileReader(output);
			
			Assert.assertFalse(reader.hasNext());
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
	}
	
	@Test(expected = FrameworkException.class)
	public void testIncorrectNumberOfObjectives() throws Exception {
		File input = TestUtils.createTempFile(COMPLETE);
		File output = TestUtils.createTempFile();
		
		ResultFileEvaluator.main(new String[] {
			"--problem", "DTLZ2_2",
			"--reference", "pf/DTLZ2.3D.pf",
			"--input", input.getAbsolutePath(),
			"--output", output.getAbsolutePath()});
	}
	
}
