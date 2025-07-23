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
package org.moeaframework.problem.BBOB2016;

import java.util.Arrays;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.junit.Assume;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TestThresholds;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.Problem;
import org.moeaframework.util.io.LineReader;
import org.moeaframework.util.io.Resources;

public class BBOB2016Test {

	@Test
	public void testSuite() {
		int[] functions = new int[] { 1, 2, 6, 8, 13, 14, 15, 17, 20, 21 };
		int[] dimensions = new int[] { 2, 3, 5, 10, 20, 40 };
		int[] instances = new int[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };

		for (int i = 0; i < functions.length; i++) {
			for (int j = i; j < functions.length; j++) {
				for (int instance : instances) {
					for (int dimension : dimensions) {
						Problem problem = new BBOB2016Problems().getProblem(String.format(
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
		Assert.assertNotNull(new BBOB2016Problems().getProblem("bbob_f1_i1_d2,bbob_f21_i15_d40"));
		Assert.assertNotNull(new BBOB2016Problems().getProblem("bbob_f1_i1_d2__bbob_f21_i15_d40"));
		Assert.assertNotNull(new BBOB2016Problems().getProblem("bbob-biobj(bbob_f1_i1_d2__bbob_f21_i15_d40)"));
	}
	
	@Test
	public void testUnrecognizedProblem() {
		Assert.assertNull(new BBOB2016Problems().getProblem(""));
		Assert.assertNull(new BBOB2016Problems().getProblem("Foo"));
		Assert.assertNull(new BBOB2016Problems().getProblem("bbob-biobj()"));
		Assert.assertNull(new BBOB2016Problems().getProblem("bbob-biobj(Foo)"));
		Assert.assertNull(new BBOB2016Problems().getProblem("bbob-biobj(bbob_f1_i1_d2__Foo)"));
		Assert.assertNull(new BBOB2016Problems().getProblem("bbob-biobj(Foo_bbob_f1_i1_d2)"));
		Assert.assertNull(new BBOB2016Problems().getProblem("bbob-biobj(bbob_fFoo_i1_d2__bbob_f21_i15_d40)"));
		Assert.assertNull(new BBOB2016Problems().getProblem("bbob-biobj(bbob_f1_iFoo_d2__bbob_f21_i15_d40)"));
		Assert.assertNull(new BBOB2016Problems().getProblem("bbob-biobj(bbob_f1_i1_dFoo__bbob_f21_i15_d40)"));
		Assert.assertNull(new BBOB2016Problems().getProblem("bbob-biobj(bbob_f1_i1_d2__bbob_fFoo_i15_d40)"));
		Assert.assertNull(new BBOB2016Problems().getProblem("bbob-biobj(bbob_f1_i1_d2__bbob_f21_iFoo_d40)"));
		Assert.assertNull(new BBOB2016Problems().getProblem("bbob-biobj(bbob_f1_i1_d2__bbob_f21_i15_dFoo)"));
	}

	@Test
	public void testEvaluation() throws Exception {
		try (LineReader reader = Resources.asLineReader(getClass(), "BBOB2016.Test.dat")) {
			for (String line : reader) {
				String[] tokens = line.split("\s+");
				int index = 0;
				
				Problem problem = new BBOB2016Problems().getProblem(tokens[index++]);
				
				Assert.assertEquals(Integer.parseInt(tokens[index++]), problem.getNumberOfVariables());
				Assert.assertEquals(Integer.parseInt(tokens[index++]), problem.getNumberOfObjectives());
				Assert.assertEquals(Integer.parseInt(tokens[index++]), problem.getNumberOfConstraints());
				
				// load sample from input
				double[] x = new double[problem.getNumberOfVariables()];
				double[] fx = new double[problem.getNumberOfObjectives()];
				double[] cx = new double[problem.getNumberOfConstraints()];
				
				for (int i = 0; i < problem.getNumberOfVariables(); i++) {
					x[i] = Double.parseDouble(tokens[index++]);
				}
				
				for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
					fx[i] = Double.parseDouble(tokens[index++]);
				}
				
				for (int i = 0; i < problem.getNumberOfConstraints(); i++) {
					cx[i] = Double.parseDouble(tokens[index++]);
				}
				
				// evaluate sample
				Solution solution = problem.newSolution();
				RealVariable.setReal(solution, x);
				problem.evaluate(solution);
				
				// compare output
				try {
					Assert.assertArrayEquals(fx, solution.getObjectiveValues(), TestThresholds.LOW_PRECISION);
					Assert.assertArrayEquals(cx, solution.getConstraintValues(), TestThresholds.LOW_PRECISION);
				} catch (AssertionError e) {
					System.out.println("Detected difference in " + problem.getName() + " (Expected / Actual):");
					System.out.println("  > Variables: " + Arrays.toString(x) + " / " + Arrays.toString(RealVariable.getReal(solution)));
					System.out.println("  > Objectives: " + Arrays.toString(fx) + " / " + Arrays.toString(solution.getObjectiveValues()));
					System.out.println("  > Constraints: " + Arrays.toString(cx) + " / " + Arrays.toString(solution.getConstraintValues()));
					
					throw e;
				}
			}
		}
	}
	
	@Test
	public void testGenerator() throws Exception {
		BBOB2016TestGenerator generator = new BBOB2016TestGenerator();
		CommandLine commandLine = new DefaultParser().parse(generator.getOptions(), new String[0]);
		
		try {
			generator.run(commandLine);
		} catch (UnsatisfiedLinkError e) {
			Assume.assumeNoException("CocoJNI is not available or configured correctly", e);
		}
	}

}
