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
package org.moeaframework.core.operator;

import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.core.Variation;

/**
 * Tests if {@link Variation} operators are type-safe.
 */
public class TypeSafetyTest {

	/**
	 * Variable for testing type safety. Since this variable is unknown to the
	 * operators, only the copy() method is valid.
	 */
	private static class NewVariable implements Variable {

		private static final long serialVersionUID = 4720298250812900192L;

		@Override
		public Variable copy() {
			return new NewVariable();
		}

		@Override
		public void randomize() {
			throw new UnsupportedOperationException();
		}

	}

	/**
	 * Tests if the specified variation operator is type safe. The variation
	 * operator should be constructed so that it is guaranteed to be applied to
	 * the parents (i.e., a probability of 1).
	 * 
	 * @param variation the variation operator to test
	 */
	public static void testTypeSafety(Variation variation) {
		Solution[] parents = new Solution[variation.getArity()];

		for (int i = 0; i < variation.getArity(); i++) {
			Solution solution = new Solution(2, 0);
			solution.setVariable(0, new NewVariable());
			solution.setVariable(1, new NewVariable());
			parents[i] = solution;
		}

		variation.evolve(parents);
	}

}
