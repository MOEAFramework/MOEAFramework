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
package org.moeaframework.analysis.tools;

import java.io.File;

import org.junit.Test;
import org.moeaframework.TestUtils;

/**
 * Integration tests for the command line utilities.  These tests only automate
 * checks to ensure the command line utilities interoperate and that their
 * command line interfaces function appropriately; not that their internal
 * behavior is valid.  Unit tests of the internal components ensure validity.
 */
public class IntegrationTest {

	@Test
	public void testARFFConverter() throws Exception {
		File resultFile = TestUtils.createTempFile();
		File arffFile = TestUtils.createTempFile();
		
		Solve.main(new String[] {
				"-a", "NSGAII",
				"-b", "DTLZ2_2",
				"-n", "1000",
				"-f", resultFile.getPath() });
		
		ARFFConverter.main(new String[] {
				"-b", "DTLZ2_2",
				"-i", resultFile.getPath(),
				"-o", arffFile.getPath() });
		
		TestUtils.assertLinePattern(arffFile, "^([@%].*)|(" +
				TestUtils.getCommaSeparatedNumericPattern(13) + ")$");
	}
	
	@Test
	public void testAerovisConverter() throws Exception {
		File resultFile = TestUtils.createTempFile();
		File aerovisFile = TestUtils.createTempFile();
		
		Solve.main(new String[] {
				"-a", "NSGAII",
				"-b", "DTLZ2_2",
				"-n", "1000",
				"-f", resultFile.getPath() });
		
		AerovisConverter.main(new String[] {
				"-b", "DTLZ2_2",
				"-i", resultFile.getPath(),
				"-o", aerovisFile.getPath() });
		
		TestUtils.assertLinePattern(aerovisFile,
				"^(#.*)|(" +
				TestUtils.getSpaceSeparatedNumericPattern(2) +
				")|(" +
				TestUtils.getSpaceSeparatedNumericPattern(13) +
				")$");
	}
	
}
