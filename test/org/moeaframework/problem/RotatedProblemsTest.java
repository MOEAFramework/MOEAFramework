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
package org.moeaframework.problem;

import org.apache.commons.math3.util.MathArrays;
import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestThresholds;
import org.moeaframework.TestUtils;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.operator.RandomInitialization;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.core.spi.ProviderNotFoundException;

/**
 * Tests the {@link RotatedProblem} and {@link RotatedProblems} classes.
 */
public class RotatedProblemsTest {
	
	@Test
	public void testReferenceSet() {
		TestUtils.assertEquals(
				ProblemFactory.getInstance().getReferenceSet("DTLZ2_2"),
				ProblemFactory.getInstance().getReferenceSet("ROT_DTLZ2_2"));
	}
	
	@Test
	public void testDifferent1() {
		assertNotEquals(
				ProblemFactory.getInstance().getProblem("ROT(RAND)_DTLZ2_2"),
				ProblemFactory.getInstance().getProblem("ROT(RAND)_DTLZ2_2"));
	}
	
	@Test
	public void testDifferent2() {
		assertNotEquals(
				ProblemFactory.getInstance().getProblem("UNROT_DTLZ2_2"),
				ProblemFactory.getInstance().getProblem("ROT_DTLZ2_2"));
	}
	
	@Test
	public void testBoundsEquals1() {
		assertBoundsEquals(
				ProblemFactory.getInstance().getProblem("UNROT_DTLZ2_2"),
				ProblemFactory.getInstance().getProblem("ROT_DTLZ2_2"));
	}
	
	@Test
	public void testBoundsEquals2() {
		assertBoundsEquals(
				ProblemFactory.getInstance().getProblem("ROT(RAND)_DTLZ2_2"),
				ProblemFactory.getInstance().getProblem("ROT(45)_DTLZ2_2"));
	}
	
	@Test
	public void testBoundsEquals3() {
		assertBoundsEquals(
				ProblemFactory.getInstance().getProblem("ROT(ALL,45)_DTLZ2_2"),
				ProblemFactory.getInstance().getProblem("ROT(2,135)_DTLZ2_2"));
	}
	
	@Test
	public void testNoRotation() {
		assertEquals(ProblemFactory.getInstance().getProblem("ROT(0)_DTLZ2_2"),
				ProblemFactory.getInstance().getProblem("ROT(360)_DTLZ2_2"));
	}

	@Test
	public void testNegativeRotations() {
		assertEquals(ProblemFactory.getInstance().getProblem("ROT(45)_DTLZ2_2"),
				ProblemFactory.getInstance().getProblem("ROT(-315)_DTLZ2_2"));
	}
	
	@Test(expected=ProviderNotFoundException.class)
	public void testInvalidAngle1() {
		ProblemFactory.getInstance().getProblem("ROT()_DTLZ2_2");
	}
	
	@Test(expected=ProviderNotFoundException.class)
	public void testInvalidAngle2() {
		ProblemFactory.getInstance().getProblem("ROT(foo)_DTLZ2_2");
	}
	
	@Test(expected=ProviderNotFoundException.class)
	public void testInvalidK1() {
		ProblemFactory.getInstance().getProblem("ROT(,45)_DTLZ2_2");
	}
	
	@Test(expected=ProviderNotFoundException.class)
	public void testInvalidK2() {
		ProblemFactory.getInstance().getProblem("ROT(foo,45)_DTLZ2_2");
	}
	
	private void assertBoundsEquals(Problem problemA, Problem problemB) {
		TestUtils.assertEquals(problemA.newSolution(), problemB.newSolution());
	}
	
	private void assertEquals(Problem problemA, Problem problemB) {
		Initialization initialization = new RandomInitialization(problemA, 1);

		for (int i=0; i<TestThresholds.SAMPLES; i++) {
			Solution solutionA = initialization.initialize()[0];
			Solution solutionB = solutionA.copy();
			
			problemA.evaluate(solutionA);
			problemB.evaluate(solutionB);
			
			TestUtils.assertEquals(solutionA, solutionB);
			Assert.assertTrue(solutionA.violatesConstraints() == 
					solutionB.violatesConstraints());
		}
	}
	
	private void assertNotEquals(Problem problemA, Problem problemB) {
		Initialization initialization = new RandomInitialization(problemA, 1);

		for (int i=0; i<TestThresholds.SAMPLES; i++) {
			Solution solutionA = initialization.initialize()[0];
			Solution solutionB = solutionA.copy();
			
			problemA.evaluate(solutionA);
			problemB.evaluate(solutionB);
			
			Assert.assertTrue(MathArrays.distance(solutionA.getObjectives(), 
					solutionB.getObjectives()) > Settings.EPS);
		}
	}
	
}
