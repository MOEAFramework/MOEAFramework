/* Copyright 2009-2023 David Hadka
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

import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.spi.ProblemFactory;

/**
 * Tests the {@link BBOB2016PRoblems} class.
 */
public class BBOB2016Test {
	
	@Test
	public void test() {
		int[] functions = new int[] { 1, 2, 6, 8, 13, 14, 15, 17, 20, 21 };
		int[] dimensions = new int[] { 2, 3, 5, 10, 20, 40 };
		int[] instances = new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };

		for (int i = 0; i < functions.length; i++) {
		    for (int j = i; j < functions.length; j++) {
		        for (int instance : instances) {
		            for (int dimension : dimensions) {
		            	Problem problem = ProblemFactory.getInstance().getProblem(String.format(
		            			"bbob_f%d_i%d_d%d,bbob_f%d_i%d_d%d",
		            			functions[i], instance, dimension, functions[j], instance, dimension));
		            	
		            	Assert.assertNotNull(problem);
		            }
		        }
		    }
		}
	}
	
	@Test
	public void testNameFormat() {
		Assert.assertNotNull(ProblemFactory.getInstance().getProblem("bbob_f1_i1_d2,bbob_f21_i15_d40"));
		Assert.assertNotNull(ProblemFactory.getInstance().getProblem("bbob_f1_i1_d2__bbob_f21_i15_d40"));
		Assert.assertNotNull(ProblemFactory.getInstance().getProblem("bbob-biobj(bbob_f1_i1_d2__bbob_f21_i15_d40)"));
	}
	
	/**
	 * This test runs against the Coco Framework <https://github.com/numbbo/coco>.  To use, run:
	 * <pre>
	 *   git clone https://github.com/numbbo/coco
     *   cd coco
     *   python do.py run-java
     *    
     *   cd code-experiments/build/java
     *   sed 's/Java_CocoJNI_/Java_org_moeaframework_problem_BBOB2016_CocoJNI_/g' CocoJNI.c > org_moeaframework_problem_BBOB2016_CocoJNI.c
     *   sed 's/Java_CocoJNI_/Java_org_moeaframework_problem_BBOB2016_CocoJNI_/g' CocoJNI.h > org_moeaframework_problem_BBOB2016_CocoJNI.h
     * </pre>
     * Then compile the shared library:
     * <pre>
     *   Windows:
     *     gcc "-Wl,--kill-at" -I $env:JAVA_HOME/include -I $env:JAVA_HOME/include/win32 -shared -o CocoJNI.dll org_moeaframework_problem_BBOB2016_CocoJNI.c
     * 
     *   Linux:
     *     gcc -I $JAVA_HOME/include -I $JAVA_HOME/include/linux -o CocoJNI.dll -fPIC -shared org_moeaframework_problem_BBOB2016_CocoJNI.c
     * </pre>
     * And put the resulting file in the Java library path.  For example, the root folder of this project is fine.
	 */
	@SuppressWarnings("resource")
	@Test
	public void testCoco() throws Exception {
		try {
			System.loadLibrary("CocoJNI");
		} catch (UnsatisfiedLinkError e) {
			Assume.assumeTrue("CocoJNI not found, please compile and place on the Java library path", false);
		}
		
		CocoJNI.cocoSetLogLevel("error");
		
		final String observer_options = 
				"result_folder: Testing_on_bbob-biobj " 
				+ "algorithm_name: Testing "
				+ "algorithm_info: \"MOEA Framework Testing\"";

		Suite suite = new Suite("bbob-biobj", "year: 2016", "dimensions: 2,3,5,10,20,40");
		Observer observer = new Observer("bbob-biobj", observer_options);
		Benchmark benchmark = new Benchmark(suite, observer);
		org.moeaframework.problem.BBOB2016.Problem rawProblem = null;
			
		while ((rawProblem = benchmark.getNextProblem()) != null) {	
			CocoProblemWrapper cocoProblem = new CocoProblemWrapper(rawProblem);
			Problem moeaProblem = ProblemFactory.getInstance().getProblem(cocoProblem.getName());
			
			System.out.println("Testing " + cocoProblem.getName());
			
			Assert.assertEquals(cocoProblem.getNumberOfVariables(), moeaProblem.getNumberOfVariables());
			Assert.assertEquals(cocoProblem.getNumberOfObjectives(), moeaProblem.getNumberOfObjectives());
			Assert.assertEquals(cocoProblem.getNumberOfConstraints(), moeaProblem.getNumberOfConstraints());
			
			for (int i = 0; i < 100; i++) {
				Solution solution1 = moeaProblem.newSolution();
				Solution solution2 = solution1.copy();
				
				moeaProblem.evaluate(solution1);
				cocoProblem.evaluate(solution2);
				
				TestUtils.assertEquals(solution1, solution2);
			}
		}
				
		benchmark.finalizeBenchmark();
		observer.finalizeObserver();
		//suite.finalizeSuite(); // This ends up terminating the JVM and interrputing the tests
	}

}
