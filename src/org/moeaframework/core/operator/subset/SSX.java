/* Copyright 2009-2016 David Hadka
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
package org.moeaframework.core.operator.subset;

import java.util.Set;

import org.moeaframework.core.PRNG;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.core.Variation;
import org.moeaframework.core.variable.Subset;

/**
 * Subset crossover.  Similar to {@code HUX} for binary strings, SSX swaps
 * half of the non-shared members of the two subsets. 
 * <p>
 * This variation operator is type-safe.
 */
public class SSX implements Variation {

	/**
	 * The probability of applying this operator.
	 */
	private final double probability;

	/**
	 * Constructs a SSX operator.
	 * 
	 * @param probability the probability of applying this operator
	 */
	public SSX(double probability) {
		super();
		this.probability = probability;
	}

	/**
	 * Evolves the specified variables using the SSX operator.
	 * 
	 * @param s1 the first subset
	 * @param s2 the second subset
	 */
	public static void evolve(Subset s1, Subset s2) {
		int k = s1.getK();
		Set<Integer> p1set = s1.getSet();
		Set<Integer> p2set = s2.getSet();
		
		for (int i = 0; i < k; i++) {
			if (!p1set.contains(s2.get(i)) && !p2set.contains(s1.get(i)) && PRNG.nextBoolean()) {
				int temp = s1.get(i);
				s1.set(i, s2.get(i));
				s2.set(i, temp);
			}
		}
	}

	@Override
	public Solution[] evolve(Solution[] parents) {
		Solution result1 = parents[0].copy();
		Solution result2 = parents[1].copy();

		for (int i = 0; i < result1.getNumberOfVariables(); i++) {
			Variable variable1 = result1.getVariable(i);
			Variable variable2 = result2.getVariable(i);

			if ((PRNG.nextDouble() <= probability)
					&& (variable1 instanceof Subset)
					&& (variable2 instanceof Subset)) {
				evolve((Subset)variable1, (Subset)variable2);
			}
		}

		return new Solution[] { result1, result2 };
	}

	@Override
	public int getArity() {
		return 2;
	}

}
