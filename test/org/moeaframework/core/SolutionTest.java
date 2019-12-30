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
package org.moeaframework.core;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.core.variable.RealVariable;

/**
 * Tests the {@link Solution} class. Due to the central role of this class, many
 * obvious properties are tested to ensure complete correctness.
 */
public class SolutionTest {

	/**
	 * The shared solution used by these tests.
	 */
	private Solution solution;

	/**
	 * Constructs the shared solution used by these tests.
	 */
	@Before
	public void setUp() {
		solution = new Solution(1, 2, 2);

		solution.setVariable(0, new RealVariable(0.5, 0.0, 1.0));
		solution.setObjective(0, 1.0);
		solution.setObjective(1, 2.0);
		solution.setConstraint(0, 0.0);
		solution.setConstraint(1, 1.0);

		solution.setAttribute("foo", "bar");
	}

	/**
	 * Removes references to shared objects so they can be garbage collected.
	 */
	@After
	public void tearDown() {
		solution = null;
	}

	/**
	 * Tests if the {@code getVariable} method returns the correct value.
	 */
	@Test
	public void testGetVariable() {
		Assert.assertEquals(1, solution.getNumberOfVariables());
		Assert.assertEquals(0.5, ((RealVariable)solution.getVariable(0))
				.getValue(), Settings.EPS);
	}

	/**
	 * Tests if the {@code getObjective} method returns the correct value.
	 */
	@Test
	public void testGetObjective() {
		Assert.assertEquals(2, solution.getNumberOfObjectives());
		Assert.assertEquals(1.0, solution.getObjective(0), Settings.EPS);
		Assert.assertEquals(2.0, solution.getObjective(1), Settings.EPS);
	}

	/**
	 * Tests if the {@code getConstraint} method returns the correct value.
	 */
	@Test
	public void testGetConstraint() {
		Assert.assertEquals(2, solution.getNumberOfConstraints());
		Assert.assertEquals(0.0, solution.getConstraint(0), Settings.EPS);
		Assert.assertEquals(1.0, solution.getConstraint(1), Settings.EPS);
	}

	/**
	 * Tests if the {@code setVariable} method sets the value correctly.
	 */
	@Test
	public void testSetVariable() {
		solution.setVariable(0, new RealVariable(0.75, 0.0, 1.0));
		Assert.assertEquals(0.75, ((RealVariable)solution.getVariable(0))
				.getValue(), Settings.EPS);
	}

	/**
	 * Tests if the {@code setObjective} method sets the value correctly.
	 */
	@Test
	public void testSetObjective() {
		solution.setObjective(1, 1.5);
		Assert.assertEquals(1.5, solution.getObjective(1), Settings.EPS);
	}

	/**
	 * Tests if the {@code setConstraint} method sets the value correctly.
	 */
	@Test
	public void testSetConstraint() {
		solution.setConstraint(1, 2.0);
		Assert.assertEquals(2.0, solution.getConstraint(1), Settings.EPS);
	}

	/**
	 * Tests if the {@code getObjectives} method returns the values correctly.
	 */
	@Test
	public void testGetObjectives() {
		double[] objectives = solution.getObjectives();

		// returned array contains correct data
		Assert.assertEquals(2, objectives.length);
		Assert.assertEquals(1.0, objectives[0], Settings.EPS);
		Assert.assertEquals(2.0, objectives[1], Settings.EPS);

		// returned array is independent from internal state
		objectives[0] = 0.0;
		Assert.assertEquals(1.0, solution.getObjective(0), Settings.EPS);
	}

	/**
	 * Tests if the {@code getConstraints} method returns the values correctly.
	 */
	@Test
	public void testGetConstraints() {
		double[] constraints = solution.getConstraints();

		// returned array contains correct data
		Assert.assertEquals(2, constraints.length);
		Assert.assertEquals(0.0, constraints[0], Settings.EPS);
		Assert.assertEquals(1.0, constraints[1], Settings.EPS);

		// returned array is independent from internal state
		constraints[0] = 1.0;
		Assert.assertEquals(0.0, solution.getConstraint(0), Settings.EPS);
	}

	/**
	 * Tests if the {@code setObjectives} method sets the values correctly.
	 */
	@Test
	public void testSetObjectives() {
		double[] objectives = new double[] { 3.0, 4.0 };
		solution.setObjectives(objectives);

		// stored array contains correct data
		Assert.assertEquals(2, solution.getNumberOfObjectives());
		Assert.assertEquals(3.0, solution.getObjective(0), Settings.EPS);
		Assert.assertEquals(4.0, solution.getObjective(1), Settings.EPS);

		// stored array is independent from external state
		objectives[0] = 0.0;
		Assert.assertEquals(3.0, solution.getObjective(0), Settings.EPS);
	}

	/**
	 * Tests if the {@code setConstraints} method sets the values correctly.
	 */
	@Test
	public void testSetConstraints() {
		double[] constraints = new double[] { 3.0, 4.0 };
		solution.setConstraints(constraints);

		// stored array contains correct data
		Assert.assertEquals(2, solution.getNumberOfConstraints());
		Assert.assertEquals(3.0, solution.getConstraint(0), Settings.EPS);
		Assert.assertEquals(4.0, solution.getConstraint(1), Settings.EPS);

		// stored array is independent from external state
		constraints[0] = 0.0;
		Assert.assertEquals(3.0, solution.getConstraint(0), Settings.EPS);
	}

	/**
	 * Tests if the {@code getAttribute} method works correctly.
	 */
	@Test
	public void testGetAttribute() {
		// get valid attribute
		Assert.assertTrue(solution.hasAttribute("foo"));
		Assert.assertEquals("bar", solution.getAttribute("foo"));

		// fail on invalid attribute
		Assert.assertFalse(solution.hasAttribute("bar"));
		Assert.assertNull(solution.getAttribute("bar"));
	}

	/**
	 * Tests if the {@code setAttribute} method works correctly.
	 */
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

	/**
	 * Tests if the {@code setVariable} method correctly detects invalid
	 * indices.
	 */
	@Test(expected = IndexOutOfBoundsException.class)
	public void testSetVariableBoundsChecking1() {
		solution.setVariable(2, new RealVariable(0.5, 0.0, 1.0));
	}

	/**
	 * Tests if the {@code setVariable} method correctly detects invalid
	 * indices.
	 */
	@Test(expected = IndexOutOfBoundsException.class)
	public void testSetVariableBoundsChecking2() {
		solution.setVariable(-1, new RealVariable(0.5, 0.0, 1.0));
	}

	/**
	 * Tests if the {@code getVariable} method correctly detects invalid
	 * indices.
	 */
	@Test(expected = IndexOutOfBoundsException.class)
	public void testGetVariableBoundsChecking1() {
		solution.getVariable(2);
	}

	/**
	 * Tests if the {@code getVariable} method correctly detects invalid
	 * indices.
	 */
	@Test(expected = IndexOutOfBoundsException.class)
	public void testGetVariableBoundsChecking2() {
		solution.getVariable(-1);
	}

	/**
	 * Tests if the {@code setObjective} method correctly detects invalid
	 * indices.
	 */
	@Test(expected = IndexOutOfBoundsException.class)
	public void testSetObjectiveBoundsChecking1() {
		solution.setObjective(2, 1.0);
	}

	/**
	 * Tests if the {@code setObjective} method correctly detects invalid
	 * indices.
	 */
	@Test(expected = IndexOutOfBoundsException.class)
	public void testSetObjectiveBoundsChecking2() {
		solution.setObjective(-1, 1.0);
	}

	/**
	 * Tests if the {@code getObjective} method correctly detects invalid
	 * indices.
	 */
	@Test(expected = IndexOutOfBoundsException.class)
	public void testGetObjectiveBoundsChecking1() {
		solution.getObjective(2);
	}

	/**
	 * Tests if the {@code getObjective} method correctly detects invalid
	 * indices.
	 */
	@Test(expected = IndexOutOfBoundsException.class)
	public void testGetObjectiveBoundsChecking2() {
		solution.getObjective(-1);
	}

	/**
	 * Tests if the {@code setObjectives} method correctly detects invalid
	 * indices.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetObjectivesBoundsChecking() {
		solution.setObjectives(new double[] { 0.0, 1.0, 2.0 });
	}

	/**
	 * Tests if the {@code setConstraint} method correctly detects invalid
	 * indices.
	 */
	@Test(expected = IndexOutOfBoundsException.class)
	public void testSetConstraintBoundsChecking1() {
		solution.setConstraint(2, 1.0);
	}

	/**
	 * Tests if the {@code setConstraint} method correctly detects invalid
	 * indices.
	 */
	@Test(expected = IndexOutOfBoundsException.class)
	public void testSetConstraintBoundsChecking2() {
		solution.setConstraint(-1, 1.0);
	}

	/**
	 * Tests if the {@code getConstraint} method correctly detects invalid
	 * indices.
	 */
	@Test(expected = IndexOutOfBoundsException.class)
	public void testGetConstraintBoundsChecking1() {
		solution.getConstraint(2);
	}

	/**
	 * Tests if the {@code getConstraint} method correctly detects invalid
	 * indices.
	 */
	@Test(expected = IndexOutOfBoundsException.class)
	public void testGetConstraintBoundsChecking2() {
		solution.getConstraint(-1);
	}

	/**
	 * Tests if the {@code setConstraints} method correctly detects invalid
	 * indices.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetConstraintsBoundsChecking() {
		solution.setConstraints(new double[] { 0.0, 1.0, 2.0 });
	}

	/**
	 * Tests if the constructor, when given an array of objectives, correctly
	 * initializes the solution.
	 */
	@Test
	public void testObjectiveConstructor() {
		double[] objectives = new double[] { 1.0, 2.0 };
		Solution solution = new Solution(objectives);

		// correct internal state
		Assert.assertEquals(0, solution.getNumberOfVariables());
		Assert.assertEquals(2, solution.getNumberOfObjectives());
		Assert.assertEquals(0, solution.getNumberOfConstraints());
		Assert.assertEquals(0, solution.getAttributes().size());
		Assert.assertEquals(1.0, solution.getObjective(0), Settings.EPS);
		Assert.assertEquals(2.0, solution.getObjective(1), Settings.EPS);

		// check if objectives were defensively copied
		objectives[0] = 0.0;
		Assert.assertEquals(1.0, solution.getObjective(0), Settings.EPS);
	}
	
	/**
	 * Tests if the deep copy method works correctly, property cloning all
	 * attributes.
	 */
	@Test
	public void testDeepCopy() {
		double[] array = new double[] { 1.0, 2.0 };
		solution.setAttribute("key", array);
		
		Solution copy = solution.deepCopy();
		
		Assert.assertTrue(array != copy.getAttribute("key"));
	}

	/**
	 * Tests if the copy constructor works correctly.
	 */
	@Test
	public void testCopyConstructor() {
		Solution copy = new Solution(solution);

		// the equals method is based on object identity
		Assert.assertFalse(solution.equals(copy));
		Assert.assertFalse(copy.equals(solution));

		// copy has the same variables
		Assert.assertEquals(solution.getNumberOfVariables(), copy
				.getNumberOfVariables());
		for (int i = 0; i < copy.getNumberOfVariables(); i++) {
			Assert.assertEquals(solution.getVariable(i), copy.getVariable(i));
		}

		// copy has the same objectives
		Assert.assertEquals(solution.getNumberOfObjectives(), copy
				.getNumberOfObjectives());
		for (int i = 0; i < copy.getNumberOfObjectives(); i++) {
			Assert.assertEquals(solution.getObjective(i), copy.getObjective(i),
					Settings.EPS);
		}

		// copy has the same constraints
		Assert.assertEquals(solution.getNumberOfConstraints(), copy
				.getNumberOfConstraints());
		for (int i = 0; i < copy.getNumberOfConstraints(); i++) {
			Assert.assertEquals(solution.getConstraint(i), copy
					.getConstraint(i), Settings.EPS);
		}

		// the copy's variables are independent from the original
		((RealVariable)copy.getVariable(0)).setValue(1.0);
		Assert.assertEquals(0.5, ((RealVariable)solution.getVariable(0))
				.getValue(), Settings.EPS);

		// the equals method works to detect the change
		Assert.assertFalse(solution.equals(copy));
		Assert.assertFalse(copy.equals(solution));
	}

	/**
	 * Tests if the {@code violatesConstraints} method returns {@code 0} if
	 * all constraints are satisfied and a non-zero value otherwise.
	 */
	@Test
	public void testViolatesConstraints() {
		Assert.assertTrue(solution.violatesConstraints());

		solution.setConstraint(1, 0.0);
		Assert.assertFalse(solution.violatesConstraints());

		// solution with no constraints
		Assert.assertFalse(new Solution(0, 0, 0).violatesConstraints());
	}

}
