/* Copyright 2009-2016 David Hadka
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
package org.moeaframework.problem.BBOB2016;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

import org.junit.Test;
import org.moeaframework.Executor;
import org.moeaframework.TestUtils;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.PRNG;

public class BBOB2016Test {
	
	@Test
	public void test() throws IOException {
		// skip test if the Coco DLL does not exist
		TestUtils.assumeFileExists(new File("CocoJNI.dll"));
		
		// capture the output to collect all BBOB2016-Biobj problem names
		PrintStream oldOutput = System.out;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		System.setOut(new PrintStream(baos));
		
		CocoProblemWrapper.printProblemNames();
		
		System.out.close();
		System.setOut(oldOutput);
		
		// parse the output and test each problem instance
		BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(baos.toByteArray())));
		String problemName = null;
		
		while ((problemName = reader.readLine()) != null) {
			PRNG.setSeed(1000);
			NondominatedPopulation result1 = new Executor()
					.withProblem(problemName)
					.withAlgorithm("NSGAII")
					.withMaxEvaluations(20000)
					.run();
			
			PRNG.setSeed(1000);
			NondominatedPopulation result2 = new Executor()
					.withProblem(CocoProblemWrapper.findProblem("NSGAII", problemName))
					.withAlgorithm("NSGAII")
					.withMaxEvaluations(20000)
					.run();
			
			System.out.println(problemName + " " + TestUtils.equals(result1, result2));
		}
	}

}
