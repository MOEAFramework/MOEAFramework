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
package org.moeaframework.problem.BBOB2016;

import java.util.Arrays;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.Assume;
import org.moeaframework.core.Solution;
import org.moeaframework.core.initialization.RandomInitialization;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.Problem;

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
	 * This test runs against the <a href="https://github.com/numbbo/coco">Coco Framework</a>.  See
	 * {@code setup-coco.sh} for instructions on compiling Coco.
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

		String observerOptions = String.join(" ",
				"result_folder: Testing_on_bbob-biobj",
				"algorithm_name: Testing",
				"algorithm_info: \"MOEA Framework Testing\"");

		Suite suite = new Suite("bbob-biobj", "year: 2016", "dimensions: 2,3,5,10,20,40");
		Observer observer = new Observer("bbob-biobj", observerOptions);
		Benchmark benchmark = new Benchmark(suite, observer);
		org.moeaframework.problem.BBOB2016.Problem rawProblem = null;

		while ((rawProblem = benchmark.getNextProblem()) != null) {
			CocoProblemWrapper cocoProblem = new CocoProblemWrapper(rawProblem);
			Problem moeaProblem = ProblemFactory.getInstance().getProblem(cocoProblem.getName());

			System.out.println("Testing COCO problem instance " + cocoProblem.getName());

			Assert.assertEquals(cocoProblem.getNumberOfVariables(), moeaProblem.getNumberOfVariables());
			Assert.assertEquals(cocoProblem.getNumberOfObjectives(), moeaProblem.getNumberOfObjectives());
			Assert.assertEquals(cocoProblem.getNumberOfConstraints(), moeaProblem.getNumberOfConstraints());
			
			Solution[] solutions = new RandomInitialization(moeaProblem).initialize(100);

			for (Solution solution : solutions) {
				Solution cocoSolution = solution.copy();

				moeaProblem.evaluate(solution);
				cocoProblem.evaluate(cocoSolution);
				
				Assert.assertNoNaN(solution);
				Assert.assertNoNaN(cocoSolution);

				try {
					Assert.assertEquals(solution, cocoSolution);
				} catch (AssertionError e) {
					System.out.println("Detected difference (MOEA Framework / Coco):");
					System.out.println("  > Variables: " + Arrays.toString(RealVariable.getReal(solution)) + " / " +
							Arrays.toString(RealVariable.getReal(cocoSolution)));
					System.out.println("  > Objectives: " + Arrays.toString(solution.getObjectiveValues()) + " / " +
							Arrays.toString(cocoSolution.getObjectiveValues()));
					System.out.println("  > Constraints: " + Arrays.toString(solution.getConstraintValues()) + " / " +
							Arrays.toString(cocoSolution.getConstraintValues()));
					
					throw e;
				}
			}
		}

		benchmark.finalizeBenchmark();
		//observer.finalizeObserver(); // This causes a "free(): double free detected in tcache 2" error
		//suite.finalizeSuite(); // This ends up terminating the JVM and interrupting the tests
	}

}
