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
package org.moeaframework.analysis.tools;

import java.io.File;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TempFiles;
import org.moeaframework.TestResources;
import org.moeaframework.analysis.io.MetricFileWriter.Metric;

public class MetricsEvaluatorTest extends AbstractToolTest {
	
	@Test
	public void testComplete() throws Exception {
		File input = TempFiles.createFile().withContent(COMPLETE_RESULT_FILE);
		File output = TempFiles.createFile();
		
		File referenceSetFile = TestResources.asFile("pf/DTLZ2.2D.pf");
		
		MetricsEvaluator.main(new String[] {
			"--problem", "DTLZ2_2",
			"--reference", referenceSetFile.getAbsolutePath(),
			"--input", input.getAbsolutePath(),
			"--output", output.getAbsolutePath()});
		
		Assert.assertLineCount(3, output);
		Assert.assertLinePattern(output, Assert.getSpaceSeparatedNumericPattern(Metric.getNumberOfMetrics()));
	}
	
	@Test
	public void testEmpty() throws Exception {
		File input = TempFiles.createFile().withContent(EMPTY_RESULT_FILE);
		File output = TempFiles.createFile();
		
		File referenceSetFile = TestResources.asFile("pf/DTLZ2.2D.pf");
		
		MetricsEvaluator.main(new String[] {
			"--problem", "DTLZ2_2",
			"--reference", referenceSetFile.getAbsolutePath(),
			"--input", input.getAbsolutePath(),
			"--output", output.getAbsolutePath()});
		
		Assert.assertLineCount(1, output);
		Assert.assertLinePattern(output, "^#");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testIncorrectNumberOfObjectives() throws Exception {
		File input = TempFiles.createFile().withContent(COMPLETE_RESULT_FILE);
		File output = TempFiles.createFile();
		
		File referenceSetFile = TestResources.asFile("pf/DTLZ2.3D.pf");
		
		MetricsEvaluator.main(new String[] {
			"--problem", "DTLZ2_2",
			"--reference", referenceSetFile.getAbsolutePath(),
			"--input", input.getAbsolutePath(),
			"--output", output.getAbsolutePath()});
	}
	
}
