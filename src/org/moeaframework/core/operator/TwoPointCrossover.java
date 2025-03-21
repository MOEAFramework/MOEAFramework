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
package org.moeaframework.core.operator;

import org.moeaframework.core.PRNG;
import org.moeaframework.core.Solution;
import org.moeaframework.core.configuration.Prefix;
import org.moeaframework.core.configuration.Property;
import org.moeaframework.core.variable.Variable;
import org.moeaframework.util.validate.Validate;

/**
 * Two-point crossover.  Two crossover points are selected and all decision variables between the two points are
 * swapped between the two parents.  The two children resulting from this swapping are returned.
 */
@TypeSafe
@Prefix("2x")
public class TwoPointCrossover implements Variation {

	/**
	 * The probability of applying this operator to solutions.
	 */
	private double probability;
	
	/**
	 * Constructs a two-point crossover operator with a 100% probability.
	 */
	public TwoPointCrossover() {
		this(1.0);
	}

	/**
	 * Constructs a two-point crossover operator with the specified probability of applying this operator to solutions.
	 * 
	 * @param probability the probability of applying this operator to solutions
	 */
	public TwoPointCrossover(double probability) {
		super();
		setProbability(probability);
	}
	
	@Override
	public String getName() {
		return "2x";
	}

	/**
	 * Returns the probability of applying this operator to solutions.
	 * 
	 * @return the probability
	 */
	public double getProbability() {
		return probability;
	}
	
	/**
	 * Sets the probability of applying this operator to solutions.
	 * 
	 * @param probability the probability between 0.0 and 1.0, inclusive
	 */
	@Property("rate")
	public void setProbability(double probability) {
		Validate.that("probability", probability).isProbability();
		this.probability = probability;
	}

	@Override
	public Solution[] evolve(Solution[] parents) {
		Solution result1 = parents[0].copy();
		Solution result2 = parents[1].copy();

		if ((PRNG.nextDouble() <= probability) && (result1.getNumberOfVariables() > 1)) {
			int crossoverPoint1 = PRNG.nextInt(result1.getNumberOfVariables() - 1);
			int crossoverPoint2 = PRNG.nextInt(result1.getNumberOfVariables() - 1);

			if (crossoverPoint1 > crossoverPoint2) {
				int temp = crossoverPoint1;
				crossoverPoint1 = crossoverPoint2;
				crossoverPoint2 = temp;
			}

			for (int i = crossoverPoint1; i <= crossoverPoint2; i++) {
				Variable temp = result1.getVariable(i);
				result1.setVariable(i, result2.getVariable(i));
				result2.setVariable(i, temp);
			}
		}

		return new Solution[] { result1, result2 };
	}

	@Override
	public int getArity() {
		return 2;
	}

}
