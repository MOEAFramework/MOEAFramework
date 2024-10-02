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
package org.moeaframework.analysis.tools;

import java.io.File;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TempFiles;
import org.moeaframework.TestResources;
import org.moeaframework.analysis.io.MetricFileReader;
import org.moeaframework.core.FrameworkException;

public class ResultFileEvaluatorTest {
	
	public static final String COMPLETE = """
			# Problem = DTLZ2_2
			# Variables = 0
			# Objectives = 2
			0.0 1.0
			1.0 0.0
			#
			0.0 1.0
			1.0 0.0
			#
			""";
	
	public static final String EMPTY = """
			# Problem = DTLZ2_2
			# Variables = 0
			# Objectives = 2
			""";
	
	@Test
	public void testComplete() throws Exception {
		File input = TempFiles.createFile().withContent(COMPLETE);
		File output = TempFiles.createFile();
		
		File referenceSetFile = TestResources.asFile("pf/DTLZ2.2D.pf");
		
		ResultFileEvaluator.main(new String[] {
			"--problem", "DTLZ2_2",
			"--reference", referenceSetFile.getAbsolutePath(),
			"--input", input.getAbsolutePath(),
			"--output", output.getAbsolutePath()});
		
		try (MetricFileReader reader = new MetricFileReader(output)) {
			Assert.assertTrue(reader.hasNext());
			Assert.assertNotNull(reader.next());
			Assert.assertTrue(reader.hasNext());
			Assert.assertNotNull(reader.next());
			Assert.assertFalse(reader.hasNext());
		}
	}
	
	@Test
	public void testEmpty() throws Exception {
		File input = TempFiles.createFile().withContent(EMPTY);
		File output = TempFiles.createFile();
		
		File referenceSetFile = TestResources.asFile("pf/DTLZ2.2D.pf");
		
		ResultFileEvaluator.main(new String[] {
			"--problem", "DTLZ2_2",
			"--reference", referenceSetFile.getAbsolutePath(),
			"--input", input.getAbsolutePath(),
			"--output", output.getAbsolutePath()});
		
		try (MetricFileReader reader = new MetricFileReader(output)) {
			Assert.assertFalse(reader.hasNext());
		}
	}
	
	@Test(expected = FrameworkException.class)
	public void testIncorrectNumberOfObjectives() throws Exception {
		File input = TempFiles.createFile().withContent(COMPLETE);
		File output = TempFiles.createFile();
		
		File referenceSetFile = TestResources.asFile("pf/DTLZ2.3D.pf");
		
		ResultFileEvaluator.main(new String[] {
			"--problem", "DTLZ2_2",
			"--reference", referenceSetFile.getAbsolutePath(),
			"--input", input.getAbsolutePath(),
			"--output", output.getAbsolutePath()});
	}
	
}
