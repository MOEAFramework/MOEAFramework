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

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.Executor;
import org.moeaframework.TestUtils;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.PRNG;

/**
 * To run these tests, you'll first need to compile CocoJNI.dll.  Copy the
 * .c and .h files from this package into the code-experiments/build/java
 * folder within the Coco Framework repository.  Then run:
 * 
 *     gcc -Wl,--kill-at -I "C:\Program Files\Java\jdk1.7.0_45\include"
 *         -I "C:\Program Files\Java\jdk1.7.0_45\include\win32" -shared
 *         -o CocoJNI.dll org_moeaframework_problem_BBOB2016_CocoJNI.c
 *         
 * You will likely need to change the paths to your version of the Java
 * Development Kit.  If any interfaces changed, you may need to update the
 * .h and .c files.  Run:
 * 
 *     javah -jni -cp test org.moeaframework.problem.BBOB2016.CocoJNI
 *     
 * from the root of the MOEA Framework source code to recreate the header file,
 * rename CocoJNI.c to org_moeaframework_problem_BBOB2016_CocoJNI.c, and
 * replace all occurrences of:
 * 
 *     Java_CocoJNI_
 *     
 * with:
 * 
 *     Java_org_moeaframework_problem_BBOB2016_CocoJNI_
 *     
 * in the C code.
 * 
 * A working version of the DLL is distributed with these tests, but must
 * first be moved to the root MOEA Framework directory.
 */
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
		
		int count = 0;
		
		while ((problemName = reader.readLine()) != null) {
			count++;
			
			PRNG.setSeed(1000);
			NondominatedPopulation result1 = new Executor()
					.withProblem(problemName)
					.withAlgorithm("NSGAII")
					.withMaxEvaluations(10000)
					.run();
			
			PRNG.setSeed(1000);
			NondominatedPopulation result2 = new Executor()
					.withProblem(CocoProblemWrapper.findProblem("NSGAII", problemName))
					.withAlgorithm("NSGAII")
					.withMaxEvaluations(10000)
					.run();
			
			boolean equal = TestUtils.equals(result1, result2);
			System.out.println(problemName + " " + (equal ? "ok!" : "does not match!") + " " + count);
			
			if (!equal) {
				Assert.fail("Output from " + problemName + " differs");
			}
			
			if (count == 100) {
				// stop after testing every instance at least once
				break;
			}
		}
	}

}
