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

import org.junit.Assert;
import org.moeaframework.TestThresholds;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.core.Variation;
import org.moeaframework.core.variable.RealVariable;

public abstract class PointCrossoverTest {

	/**
	 * Tests the specified crossover operator.
	 * 
	 * @param numberOfVariables the number of decision variables
	 * @param variation the crossover operator
	 */
	protected void test(Variation variation, int numberOfVariables) {
		for (int i = 0; i < TestThresholds.SAMPLES; i++) {
			Solution[] parents = createParents(numberOfVariables);
			Solution[] offspring = variation.evolve(parents);
			testPointCrossover(parents, offspring);
		}
	}

	/**
	 * Tests if the offspring resulted from a crossover between the parents.
	 * Requires two parents and two offspring.
	 * 
	 * @param parents the two parents
	 * @param offspring the two offspring
	 */
	protected void testPointCrossover(Solution[] parents, Solution[] offspring) {
		int n = parents[0].getNumberOfVariables();

		for (int i = 0; i < n; i++) {
			Variable p1 = parents[0].getVariable(i);
			Variable p2 = parents[1].getVariable(i);
			Variable o1 = offspring[0].getVariable(i);
			Variable o2 = offspring[1].getVariable(i);

			Assert.assertTrue((o1.equals(p1) && o2.equals(p2))
					|| (o1.equals(p2) && o2.equals(p1)));
		}
	}

	/**
	 * Returns two parent solutions for testing purposes.
	 * 
	 * @param numberOfVariables the number of decision variables
	 * @return two parent solutions for testing purposes
	 */
	protected Solution[] createParents(int numberOfVariables) {
		Solution parent1 = new Solution(numberOfVariables, 0);
		Solution parent2 = new Solution(numberOfVariables, 0);

		for (int i = 0; i < numberOfVariables; i++) {
			parent1.setVariable(i, new RealVariable(0.0, 0.0, 1.0));
			parent2.setVariable(i, new RealVariable(1.0, 0.0, 1.0));
		}

		return new Solution[] { parent1, parent2 };
	}

	/**
	 * Tests if the parents remain unchanged during variation.
	 */
	protected void testParentImmutability(Variation variation) {
		ParentImmutabilityTest.test(createParents(10), variation);
	}

}
