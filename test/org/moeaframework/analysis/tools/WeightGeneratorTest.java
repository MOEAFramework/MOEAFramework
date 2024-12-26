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

import org.apache.commons.cli.MissingOptionException;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.Assume;
import org.moeaframework.TempFiles;

public class WeightGeneratorTest {

	@Test
	public void testRandom() throws Exception {
		File resultFile = TempFiles.createFile();
		
		WeightGenerator.main(new String[] {
				"-m", "random",
				"-d", "2",
				"-n", "100",
				"-o", resultFile.getPath() });
		
		Assert.assertLinePattern(resultFile, Assert.getSpaceSeparatedNumericPattern(2));
		Assert.assertLineCount(100, resultFile);
	}
	
	@Test
	public void testNBI() throws Exception {
		File resultFile = TempFiles.createFile();
		
		WeightGenerator.main(new String[] {
				"-m", "normalboundary",
				"-d", "3",
				"--divisions", "20",
				"-o", resultFile.getPath() });
		
		Assert.assertLinePattern(resultFile, Assert.getSpaceSeparatedNumericPattern(3));
		Assert.assertLineCount(231, resultFile);
	}
	
	@Test
	public void testGeneralized() throws Exception {
		Assume.assumePythonExists();
		Assume.assumePythonModule("cvxopt");
		
		File resultFile = TempFiles.createFile();
		
		WeightGenerator.main(new String[] {
				"-m", "normalboundary",
				"-d", "3",
				"--divisions", "20",
				"--generalized",
				"-o", resultFile.getPath() });
		
		Assert.assertLinePattern(resultFile, Assert.getSpaceSeparatedNumericPattern(3));
		Assert.assertLineCount(231, resultFile);
	}
	
	@Test(expected = MissingOptionException.class)
	public void testMissingDivisions() throws Exception {
		File resultFile = TempFiles.createFile();
		
		WeightGenerator.main(new String[] {
				"-m", "normalboundary",
				"-d", "3",
				"-o", resultFile.getPath() });
		
		Assert.assertLinePattern(resultFile, Assert.getSpaceSeparatedNumericPattern(3));
		Assert.assertLineCount(230, resultFile);
	}

}
