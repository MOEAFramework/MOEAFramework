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
package org.moeaframework.algorithm.jmetal;

import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.core.Population;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.BinaryVariable;
import org.moeaframework.core.variable.Permutation;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.MockBinaryProblem;
import org.moeaframework.problem.MockConstraintProblem;
import org.moeaframework.problem.MockMultiTypeProblem;
import org.moeaframework.problem.MockPermutationProblem;
import org.moeaframework.problem.MockRealProblem;
import org.moeaframework.problem.MockSubsetProblem;
import org.moeaframework.problem.ProblemException;
import org.uma.jmetal.util.front.Front;

/**
 * Tests the {@link JMetalUtils} class.
 */
public class JMetalUtilsTest {
	
	@Test
	public void testGetTypesReal() {
		Set<Class<?>> types = JMetalUtils.getTypes(new MockRealProblem());
		
		Assert.assertEquals(1, types.size());
		Assert.assertEquals(RealVariable.class, types.iterator().next());
	}

	@Test
	public void testGetTypesBinary() {
		Set<Class<?>> types = JMetalUtils.getTypes(new MockBinaryProblem());
		
		Assert.assertEquals(1, types.size());
		Assert.assertEquals(BinaryVariable.class, types.iterator().next());
	}
	
	@Test
	public void testGetTypesPermutation() {
		Set<Class<?>> types = JMetalUtils.getTypes(new MockPermutationProblem());
		
		Assert.assertEquals(1, types.size());
		Assert.assertEquals(Permutation.class, types.iterator().next());
	}
	
	@Test
	public void testGetTypesMultiple() {
		Set<Class<?>> types = JMetalUtils.getTypes(new MockMultiTypeProblem());
		
		Assert.assertEquals(3, types.size());
		Assert.assertTrue(types.contains(RealVariable.class));
		Assert.assertTrue(types.contains(BinaryVariable.class));
		Assert.assertTrue(types.contains(Permutation.class));
	}
	
	@Test
	public void testGetSingleTypeReal() {
		Class<?> type = JMetalUtils.getSingleType(new MockRealProblem());
		
		Assert.assertEquals(RealVariable.class, type);
	}

	@Test
	public void testGetSingleTypeBinary() {
		Class<?> type = JMetalUtils.getSingleType(new MockBinaryProblem());
		
		Assert.assertEquals(BinaryVariable.class, type);
	}
	
	@Test
	public void testGetSingleTypePermutation() {
		Class<?> type = JMetalUtils.getSingleType(new MockPermutationProblem());
		
		Assert.assertEquals(Permutation.class, type);
	}
	
	@Test(expected=ProblemException.class)
	public void testGetSingleTypeMultiple() {
		JMetalUtils.getSingleType(new MockMultiTypeProblem());
	}
	
	@Test
	public void testCreateProblemAdapterReal() {
		ProblemAdapter<?> adapter = JMetalUtils.createProblemAdapter(new MockRealProblem());
		
		Assert.assertEquals(DoubleProblemAdapter.class, adapter.getClass());
	}
	
	@Test
	public void testCreateProblemAdapterBinary() {
		ProblemAdapter<?> adapter = JMetalUtils.createProblemAdapter(new MockBinaryProblem());
		
		Assert.assertEquals(BinaryProblemAdapter.class, adapter.getClass());
	}
	
	@Test
	public void testCreateProblemAdapterPermutation() {
		ProblemAdapter<?> adapter = JMetalUtils.createProblemAdapter(new MockPermutationProblem());
		
		Assert.assertEquals(PermutationProblemAdapter.class, adapter.getClass());
	}
	
	@Test(expected=ProblemException.class)
	public void testCreateProblemAdapterUnsupported() {
		JMetalUtils.createProblemAdapter(new MockSubsetProblem());
	}
	
	@Test
	public void testCopyObjectivesAndConstraints_NoConstraint_MOEAToJMetal() {
		MockRealProblem problem = new MockRealProblem();
		DoubleProblemAdapter adapter = new DoubleProblemAdapter(problem);

		Solution mySolution = problem.newSolution();
		org.uma.jmetal.solution.DoubleSolution theirSolution =
				new org.uma.jmetal.solution.impl.DefaultDoubleSolution(adapter);
		
		problem.evaluate(mySolution);
		JMetalUtils.copyObjectivesAndConstraints(mySolution, theirSolution);
		
		Assert.assertEquals(5.0, theirSolution.getObjective(0), Settings.EPS);
		Assert.assertEquals(0.0, JMetalUtils.getOverallConstraintViolation(theirSolution), Settings.EPS);
		Assert.assertEquals(0, JMetalUtils.getNumberOfViolatedConstraints(theirSolution));
	}
	
	@Test
	public void testCopyObjectivesAndConstraints_WithConstraint_MOEAToJMetal() {
		MockConstraintProblem problem = new MockConstraintProblem();
		DoubleProblemAdapter adapter = new DoubleProblemAdapter(problem);

		Solution mySolution = problem.newSolution();
		org.uma.jmetal.solution.DoubleSolution theirSolution =
				new org.uma.jmetal.solution.impl.DefaultDoubleSolution(adapter);
		
		problem.evaluate(mySolution);
		JMetalUtils.copyObjectivesAndConstraints(mySolution, theirSolution);
		
		Assert.assertEquals(5.0, theirSolution.getObjective(0), Settings.EPS);
		Assert.assertEquals(-35.0, JMetalUtils.getOverallConstraintViolation(theirSolution), Settings.EPS);
		Assert.assertEquals(2, JMetalUtils.getNumberOfViolatedConstraints(theirSolution));
	}
	
	@Test
	public void testCopyObjectivesAndConstraints_NoConstraint_JMetalToMOEA() {
		MockRealProblem problem = new MockRealProblem();
		DoubleProblemAdapter adapter = new DoubleProblemAdapter(problem);

		Solution mySolution = problem.newSolution();
		org.uma.jmetal.solution.DoubleSolution theirSolution =
				new org.uma.jmetal.solution.impl.DefaultDoubleSolution(adapter);
		
		adapter.evaluate(theirSolution);
		JMetalUtils.copyObjectivesAndConstraints(theirSolution, mySolution);
		
		Assert.assertEquals(5.0, mySolution.getObjective(0), Settings.EPS);
		Assert.assertEquals(false, mySolution.violatesConstraints());
	}
	
	@Test
	public void testCopyObjectivesAndConstraints_WithConstraint_JMetalToMOEA() {
		MockConstraintProblem problem = new MockConstraintProblem();
		DoubleProblemAdapter adapter = new DoubleProblemAdapter(problem);

		Solution mySolution = problem.newSolution();
		org.uma.jmetal.solution.DoubleSolution theirSolution =
				new org.uma.jmetal.solution.impl.DefaultDoubleSolution(adapter);
		
		adapter.evaluate(theirSolution);
		JMetalUtils.copyObjectivesAndConstraints(theirSolution, mySolution);
		
		Assert.assertEquals(5.0, mySolution.getObjective(0), Settings.EPS);
		Assert.assertEquals(true, mySolution.violatesConstraints());
	}
	
	@Test
	public void testOverallConstraintViolation() {
		MockConstraintProblem problem = new MockConstraintProblem();
		DoubleProblemAdapter adapter = new DoubleProblemAdapter(problem);

		org.uma.jmetal.solution.DoubleSolution solution =
				new org.uma.jmetal.solution.impl.DefaultDoubleSolution(adapter);
		
		Assert.assertEquals(0.0, JMetalUtils.getOverallConstraintViolation(solution), Settings.EPS);
		
		JMetalUtils.setOverallConstraintViolation(solution, -100.0);
		Assert.assertEquals(-100.0, JMetalUtils.getOverallConstraintViolation(solution), Settings.EPS);
	}
	
	@Test
	public void testNumberOfViolatedConstraints() {
		MockConstraintProblem problem = new MockConstraintProblem();
		DoubleProblemAdapter adapter = new DoubleProblemAdapter(problem);

		org.uma.jmetal.solution.DoubleSolution solution =
				new org.uma.jmetal.solution.impl.DefaultDoubleSolution(adapter);
		
		Assert.assertEquals(0, JMetalUtils.getNumberOfViolatedConstraints(solution));
		
		JMetalUtils.setNumberOfViolatedConstraints(solution, 3);
		Assert.assertEquals(3, JMetalUtils.getNumberOfViolatedConstraints(solution));
	}
	
	@Test
	public void testToFront() {
		MockRealProblem problem = new MockRealProblem(3);
		DoubleProblemAdapter adapter = new DoubleProblemAdapter(problem);
		
		Population population = new Population();
		population.add(new Solution(new double[] { 0.0, 1.0, 0.5 }));
		population.add(new Solution(new double[] { 1.0, 0.0, 0.5 }));
		
		Front front = JMetalUtils.toFront(adapter, population);
		
		Assert.assertEquals(3, front.getPointDimensions());
		Assert.assertEquals(2, front.getNumberOfPoints());
		
		for (int i = 0; i < front.getNumberOfPoints(); i++) {
			Assert.assertArrayEquals(population.get(i).getObjectives(), front.getPoint(i).getValues(), Settings.EPS);
		}
	}

}
