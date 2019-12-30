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
import java.io.IOException;

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.analysis.sensitivity.ResultFileReader;
import org.moeaframework.core.spi.ProblemFactory;

/**
 * Tests the {@link Solve} class.
 */
public class SolveTest {
	
	@Test
	public void testInternalProblem() throws Exception {
		File outputFile = TestUtils.createTempFile();
		
		Solve.main(new String[] {
				"-a", "NSGAII",
				"-b", "DTLZ2_2",
				"-n", "1000",
				"-f", outputFile.getPath() });
		
		checkOutput(outputFile);
	}
	
	@Test
	public void testExternalProblem1() throws Exception {
		Assume.assumeTrue(new File("./examples/dtlz2_stdio.exe").exists());
		File outputFile = TestUtils.createTempFile();
		
		Solve.main(new String[] {
				"-a", "NSGAII",
				"-l", "0,0,0,0,0,0,0,0,0,0,0",
				"-u", "1,1,1,1,1,1,1,1,1,1,1",
				"-o", "2",
				"-n", "1000",
				"-f", outputFile.getPath(),
				"./examples/dtlz2_stdio.exe"});
		
		checkOutput(outputFile);
	}
	
	@Test
	public void testExternalProblem2() throws Exception {
		Assume.assumeTrue(new File("./examples/dtlz2_stdio.exe").exists());
		File outputFile = TestUtils.createTempFile();
		
		Solve.main(new String[] {
				"-a", "NSGAII",
				"-v", "R(0;1),R(0;1),R(0;1),R(0;1),R(0;1),R(0;1),R(0;1),R(0;1),R(0;1),R(0;1),R(0;1)",
				"-o", "2",
				"-n", "1000",
				"-f", outputFile.getPath(),
				"./examples/dtlz2_stdio.exe"});
		
		checkOutput(outputFile);
	}
	
	private void checkOutput(File outputFile) throws IOException {
		int count = 0;
		ResultFileReader reader = null;
		
		try {
			reader = new ResultFileReader(
					ProblemFactory.getInstance().getProblem("DTLZ2_2"),
					outputFile);
		
			while (reader.hasNext()) {
				Assert.assertTrue(reader.next().getPopulation().size() > 0);
				count++;
			}
		} finally {
			if (reader != null) {
				reader.close();
			}
		}
		
		Assert.assertEquals(count, 10);
	}

}
