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
package org.moeaframework.core.operator;

import org.junit.Ignore;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.Assume;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.Variable;
import org.moeaframework.mock.MockUnsupportedVariable;

@Ignore("Abstract test class")
public abstract class AbstractOperatorTest<T extends Variation, V extends Variable> {
	
	/**
	 * Creates a new instance of this operator.  For testing purposes, the instance should have a 100% probability.
	 */
	public abstract T createInstance();
	
	/**
	 * Creates a new instance of the decision variable for testing purposes.
	 */
	public abstract V createTestVariable();
	
	/**
	 * Indicates if this operator is type safe, enabling certain test conditions.
	 * 
	 * @return {@code true} if the operator is type safe; {@code false} otherwise
	 */
	public boolean isTypeSafe() {
		return true;
	}
	
	/**
	 * Tests if the specified variation operator is type safe. The variation operator should be constructed so that it
	 * is guaranteed to be applied to the parents (i.e., a probability of 1).
	 */
	@Test
	public void testTypeSafety() {
		Assume.assumeTrue("Operator is not type safe, skipping test", isTypeSafe());
		
		T variation = createInstance();
		
		Solution[] parents = new Solution[variation.getArity()];

		for (int i = 0; i < variation.getArity(); i++) {
			Solution solution = new Solution(2, 0);
			solution.setVariable(0, new NewVariable());
			solution.setVariable(1, new NewVariable());
			parents[i] = solution;
		}

		variation.evolve(parents);
	}
	
	/**
	 * Tests if the parents remain unchanged during variation.
	 */
	@Test
	public void testParentImmutability() {
		T variation = createInstance();
		
		Solution[] parents = new Solution[variation.getArity()];
		Solution[] parentClones = new Solution[variation.getArity()];
		
		for (int i = 0; i < variation.getArity(); i++) {
			Solution solution = new Solution(1, 0);
			solution.setVariable(0, createTestVariable());
			
			parents[i] = solution;
			parentClones[i] = solution.copy();
		}
		
		Solution[] offspring = variation.evolve(parents);

		// the parents and offspring are separate entities
		for (int i = 0; i < offspring.length; i++) {
			for (int j = 0; j < parents.length; j++) {
				Assert.assertNotSame(offspring[i], parents[j]);
			}
		}

		// the parents remain unchanged
		for (int i = 0; i < parents.length; i++) {
			Assert.assertEquals(parents[i], parentClones[i]);
		}
	}

	/**
	 * Variable for testing type safety. Since this variable is unknown to the operators, only the copy() method is
	 * valid.
	 */
	private static class NewVariable extends MockUnsupportedVariable {

		private static final long serialVersionUID = 4720298250812900192L;

		@Override
		public Variable copy() {
			return new NewVariable();
		}

	}

}
