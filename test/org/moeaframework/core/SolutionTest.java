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
package org.moeaframework.core;

import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TestThresholds;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.mock.MockSolution;

/**
 * Due to the central role of this class, many obvious properties are tested to ensure complete correctness.
 */
public class SolutionTest {

	private Solution solution;

	@Before
	public void setUp() {
		solution = new Solution(1, 2, 2);

		solution.setVariable(0, new RealVariable(0.5, 0.0, 1.0));
		solution.setObjectiveValue(0, 1.0);
		solution.setObjectiveValue(1, 2.0);
		solution.setConstraintValue(0, 0.0);
		solution.setConstraintValue(1, 1.0);

		solution.setAttribute("foo", "bar");
	}

	@After
	public void tearDown() {
		solution = null;
	}

	@Test
	public void testGetVariable() {
		Assert.assertEquals(1, solution.getNumberOfVariables());
		Assert.assertEquals(0.5, ((RealVariable)solution.getVariable(0)).getValue(), TestThresholds.HIGH_PRECISION);
	}

	@Test
	public void testGetObjectiveValue() {
		Assert.assertEquals(2, solution.getNumberOfObjectives());
		Assert.assertEquals(1.0, solution.getObjectiveValue(0), TestThresholds.HIGH_PRECISION);
		Assert.assertEquals(2.0, solution.getObjectiveValue(1), TestThresholds.HIGH_PRECISION);
	}

	@Test
	public void testGetConstraintValue() {
		Assert.assertEquals(2, solution.getNumberOfConstraints());
		Assert.assertEquals(0.0, solution.getConstraintValue(0), TestThresholds.HIGH_PRECISION);
		Assert.assertEquals(1.0, solution.getConstraintValue(1), TestThresholds.HIGH_PRECISION);
	}

	@Test
	public void testSetVariable() {
		solution.setVariable(0, new RealVariable(0.75, 0.0, 1.0));
		Assert.assertEquals(0.75, ((RealVariable)solution.getVariable(0)).getValue(), TestThresholds.HIGH_PRECISION);
	}

	@Test
	public void testSetObjectiveValue() {
		solution.setObjectiveValue(1, 1.5);
		Assert.assertEquals(1.5, solution.getObjectiveValue(1), TestThresholds.HIGH_PRECISION);
	}

	@Test
	public void testSetConstraintValue() {
		solution.setConstraintValue(1, 2.0);
		Assert.assertEquals(2.0, solution.getConstraintValue(1), TestThresholds.HIGH_PRECISION);
	}

	@Test
	public void testGetObjectiveValues() {
		double[] objectives = solution.getObjectiveValues();

		// returned array contains correct data
		Assert.assertEquals(2, objectives.length);
		Assert.assertEquals(1.0, objectives[0], TestThresholds.HIGH_PRECISION);
		Assert.assertEquals(2.0, objectives[1], TestThresholds.HIGH_PRECISION);

		// returned array is independent from internal state
		objectives[0] = 0.0;
		Assert.assertEquals(1.0, solution.getObjectiveValue(0), TestThresholds.HIGH_PRECISION);
	}
	
	@Test
	public void testGetConstraintValues() {
		double[] constraints = solution.getConstraintValues();

		// returned array contains correct data
		Assert.assertEquals(2, constraints.length);
		Assert.assertEquals(0.0, constraints[0], TestThresholds.HIGH_PRECISION);
		Assert.assertEquals(1.0, constraints[1], TestThresholds.HIGH_PRECISION);

		// returned array is independent from internal state
		constraints[0] = 1.0;
		Assert.assertEquals(0.0, solution.getConstraintValue(0), TestThresholds.HIGH_PRECISION);
	}

	@Test
	public void testSetObjectiveValues() {
		double[] objectives = new double[] { 3.0, 4.0 };
		solution.setObjectiveValues(objectives);

		// stored array contains correct data
		Assert.assertEquals(2, solution.getNumberOfObjectives());
		Assert.assertEquals(3.0, solution.getObjectiveValue(0), TestThresholds.HIGH_PRECISION);
		Assert.assertEquals(4.0, solution.getObjectiveValue(1), TestThresholds.HIGH_PRECISION);

		// stored array is independent from external state
		objectives[0] = 0.0;
		Assert.assertEquals(3.0, solution.getObjectiveValue(0), TestThresholds.HIGH_PRECISION);
	}

	@Test
	public void testSetConstraintValues() {
		double[] constraints = new double[] { 3.0, 4.0 };
		solution.setConstraintValues(constraints);

		// stored array contains correct data
		Assert.assertEquals(2, solution.getNumberOfConstraints());
		Assert.assertEquals(3.0, solution.getConstraintValue(0), TestThresholds.HIGH_PRECISION);
		Assert.assertEquals(4.0, solution.getConstraintValue(1), TestThresholds.HIGH_PRECISION);

		// stored array is independent from external state
		constraints[0] = 0.0;
		Assert.assertEquals(3.0, solution.getConstraintValue(0), TestThresholds.HIGH_PRECISION);
	}

	@Test
	public void testGetAttribute() {
		// get valid attribute
		Assert.assertTrue(solution.hasAttribute("foo"));
		Assert.assertEquals("bar", solution.getAttribute("foo"));

		// fail on invalid attribute
		Assert.assertFalse(solution.hasAttribute("bar"));
		Assert.assertNull(solution.getAttribute("bar"));
	}

	@Test
	public void testSetAttribute() {
		// overwriting an attribute
		solution.setAttribute("foo", "other");
		Assert.assertEquals("other", solution.getAttribute("foo"));

		// adding a new attribute
		solution.setAttribute("bar", "new");
		Assert.assertTrue(solution.hasAttribute("bar"));
		Assert.assertEquals("new", solution.getAttribute("bar"));

		// clearing attributes
		solution.clearAttributes();
		Assert.assertEquals(0, solution.getAttributes().size());
		Assert.assertFalse(solution.hasAttribute("foo"));
		Assert.assertNull(solution.getAttribute("foo"));
	}
	
	@Test
	public void testRemoveAttribute() {
		solution.setAttribute("foo", "other");
		solution.removeAttribute("foo");
		Assert.assertFalse(solution.hasAttribute("foo"));
		Assert.assertNull(solution.getAttribute("foo"));
	}
	
	@Test
	public void testAddAttributes() {
		solution.addAttributes(Map.of("foo", "other"));
		Assert.assertEquals("other", solution.getAttribute("foo"));
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testSetVariableBoundsChecking1() {
		solution.setVariable(2, new RealVariable(0.5, 0.0, 1.0));
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testSetVariableBoundsChecking2() {
		solution.setVariable(-1, new RealVariable(0.5, 0.0, 1.0));
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testGetVariableBoundsChecking1() {
		solution.getVariable(2);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testGetVariableBoundsChecking2() {
		solution.getVariable(-1);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testSetObjectiveBoundsChecking1() {
		solution.setObjectiveValue(2, 1.0);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testSetObjectiveBoundsChecking2() {
		solution.setObjectiveValue(-1, 1.0);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testGetObjectiveBoundsChecking1() {
		solution.getObjective(2);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testGetObjectiveBoundsChecking2() {
		solution.getObjective(-1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetObjectivesBoundsChecking() {
		solution.setObjectiveValues(new double[] { 0.0, 1.0, 2.0 });
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testSetConstraintBoundsChecking1() {
		solution.setConstraintValue(2, 1.0);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testSetConstraintBoundsChecking2() {
		solution.setConstraintValue(-1, 1.0);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testGetConstraintBoundsChecking1() {
		solution.getConstraint(2);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void testGetConstraintBoundsChecking2() {
		solution.getConstraint(-1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetConstraintsBoundsChecking() {
		solution.setConstraintValues(new double[] { 0.0, 1.0, 2.0 });
	}
	
	@Test
	public void testDeepCopy() {
		double[] array = new double[] { 1.0, 2.0 };
		solution.setAttribute("key", array);
		
		Solution copy = solution.deepCopy();
		
		Assert.assertEquals(solution, copy, true);
	}

	@Test
	public void testCopyConstructor() {
		Solution copy = new Solution(solution);

		// the equals method is based on object identity
		Assert.assertFalse(solution.equals(copy));
		Assert.assertFalse(copy.equals(solution));

		// copy has the same variables
		Assert.assertEquals(solution.getNumberOfVariables(), copy.getNumberOfVariables());
		for (int i = 0; i < copy.getNumberOfVariables(); i++) {
			Assert.assertEquals(solution.getVariable(i), copy.getVariable(i));
		}

		// copy has the same objectives
		Assert.assertEquals(solution.getNumberOfObjectives(), copy.getNumberOfObjectives());
		for (int i = 0; i < copy.getNumberOfObjectives(); i++) {
			Assert.assertEquals(solution.getObjective(i), copy.getObjective(i), TestThresholds.HIGH_PRECISION);
		}

		// copy has the same constraints
		Assert.assertEquals(solution.getNumberOfConstraints(), copy.getNumberOfConstraints());
		for (int i = 0; i < copy.getNumberOfConstraints(); i++) {
			Assert.assertEquals(solution.getConstraint(i), copy.getConstraint(i), TestThresholds.HIGH_PRECISION);
		}

		// the copy's variables are independent from the original
		((RealVariable)copy.getVariable(0)).setValue(1.0);
		Assert.assertEquals(0.5, ((RealVariable)solution.getVariable(0)).getValue(), TestThresholds.HIGH_PRECISION);

		// the equals method works to detect the change
		Assert.assertFalse(solution.equals(copy));
		Assert.assertFalse(copy.equals(solution));
	}
	
	@Test
	public void testIsFeasible() {
		Assert.assertFalse(solution.isFeasible());

		solution.setConstraintValue(1, 0.0);
		Assert.assertTrue(solution.isFeasible());

		// solution with no constraints
		Assert.assertTrue(new Solution(0, 0, 0).isFeasible());
	}

	@Test
	public void testViolatesConstraints() {
		Assert.assertTrue(solution.violatesConstraints());

		solution.setConstraintValue(1, 0.0);
		Assert.assertFalse(solution.violatesConstraints());

		// solution with no constraints
		Assert.assertFalse(new Solution(0, 0, 0).violatesConstraints());
	}
	
	@Test
	public void testSumOfConstraintViolations() {
		Assert.assertEquals(1.0, solution.getSumOfConstraintViolations(), TestThresholds.HIGH_PRECISION);
		
		solution.setConstraintValue(0, -1.0);
		Assert.assertEquals(2.0, solution.getSumOfConstraintViolations(), TestThresholds.HIGH_PRECISION);

		solution.setConstraintValues(new double[] { 0.0, 0.0 });
		Assert.assertEquals(0.0, solution.getSumOfConstraintViolations(), TestThresholds.HIGH_PRECISION);

		// solution with no constraints
		Assert.assertEquals(0.0, new Solution(0, 0, 0).getSumOfConstraintViolations(), TestThresholds.HIGH_PRECISION);
	}
	
	@Test
	public void testEuclideanDistance() {
		Solution s1 = MockSolution.of().withObjectives(0.0, 1.0, 0.0);
		Solution s2 = MockSolution.of().withObjectives(0.0, 0.0, -1.0);

		Assert.assertEquals(Math.sqrt(2.0), s1.euclideanDistance(s2), TestThresholds.HIGH_PRECISION);
		Assert.assertEquals(Math.sqrt(2.0), s2.euclideanDistance(s1), TestThresholds.HIGH_PRECISION);
		Assert.assertEquals(0.0, s1.euclideanDistance(s1), TestThresholds.HIGH_PRECISION);
		Assert.assertEquals(0.0, s2.euclideanDistance(s2), TestThresholds.HIGH_PRECISION);
	}
	
	@Test
	public void testManhattanDistance() {
		Solution s1 = MockSolution.of().withObjectives(0.0, 1.0, 0.0);
		Solution s2 = MockSolution.of().withObjectives(0.0, 0.0, -1.0);

		Assert.assertEquals(2.0, s1.manhattanDistance(s2), TestThresholds.HIGH_PRECISION);
		Assert.assertEquals(2.0, s2.manhattanDistance(s1), TestThresholds.HIGH_PRECISION);
		Assert.assertEquals(0.0, s1.manhattanDistance(s1), TestThresholds.HIGH_PRECISION);
		Assert.assertEquals(0.0, s2.manhattanDistance(s2), TestThresholds.HIGH_PRECISION);
	}
	
	@Test
	public void testChebyshevDistance() {
		Solution s1 = MockSolution.of().withObjectives(0.0, 1.0, 0.0);
		Solution s2 = MockSolution.of().withObjectives(0.0, 0.0, -1.0);

		Assert.assertEquals(1.0, s1.chebyshevDistance(s2), TestThresholds.HIGH_PRECISION);
		Assert.assertEquals(1.0, s2.chebyshevDistance(s1), TestThresholds.HIGH_PRECISION);
		Assert.assertEquals(0.0, s1.chebyshevDistance(s1), TestThresholds.HIGH_PRECISION);
		Assert.assertEquals(0.0, s2.chebyshevDistance(s2), TestThresholds.HIGH_PRECISION);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testEuclideanDistanceThrowsIfLengthDiffers() {
		Solution s1 = MockSolution.of().withObjectives(0.0, 1.0, 0.0);
		Solution s2 = MockSolution.of().withObjectives(0.0, -1.0);
		s1.euclideanDistance(s2);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testManhattanDistanceThrowsIfLengthDiffers() {
		Solution s1 = MockSolution.of().withObjectives(0.0, 1.0, 0.0);
		Solution s2 = MockSolution.of().withObjectives(0.0, -1.0);
		s1.manhattanDistance(s2);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testChebyshevDistanceThrowsIfLengthDiffers() {
		Solution s1 = MockSolution.of().withObjectives(0.0, 1.0, 0.0);
		Solution s2 = MockSolution.of().withObjectives(0.0, -1.0);
		s1.chebyshevDistance(s2);
	}
	
	@Test
	public void testDistanceToNearestSolution() {
		NondominatedPopulation population = new NondominatedPopulation();
		population.add(MockSolution.of().withObjectives(0.0, 1.0));
		population.add(MockSolution.of().withObjectives(1.0, 0.0));
		
		Assert.assertEquals(0.0,
				MockSolution.of().withObjectives(0.0, 1.0).distanceToNearestSolution(population),
				TestThresholds.HIGH_PRECISION);
		
		Assert.assertEquals(0.0,
				MockSolution.of().withObjectives(1.0, 0.0).distanceToNearestSolution(population),
				TestThresholds.HIGH_PRECISION);
		
		Assert.assertEquals(Math.sqrt(0.5),
				MockSolution.of().withObjectives(0.5, 0.5).distanceToNearestSolution(population),
				TestThresholds.HIGH_PRECISION);
		
		Assert.assertEquals(Math.sqrt(0.125),
				MockSolution.of().withObjectives(0.25, 0.75).distanceToNearestSolution(population),
				TestThresholds.HIGH_PRECISION);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testDistanceToNearestSolutionThrowsIfLengthDiffers() {
		NondominatedPopulation population = new NondominatedPopulation();
		population.add(MockSolution.of().withObjectives(0.0, 1.0));
		population.add(MockSolution.of().withObjectives(1.0, 0.0));
		
		MockSolution.of().withObjectives(0.0, 1.0, 0.5).distanceToNearestSolution(population);
	}

}
