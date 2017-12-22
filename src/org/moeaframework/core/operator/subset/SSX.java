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

import java.util.HashSet;
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
		Set<Integer> s1set = s1.getSet();
		Set<Integer> s2set = s2.getSet();
		
		Set<Integer> intersection = new HashSet<Integer>(s1set);
		intersection.retainAll(s2set);
		
		s1set.removeAll(intersection);
		s2set.removeAll(intersection);
		
		while (!s1set.isEmpty() && !s2set.isEmpty()) {
			int s1member = PRNG.nextItem(s1set);
			int s2member = PRNG.nextItem(s2set);
			
			s1.replace(s1member, s2member);
			s2.replace(s2member, s1member);
			
			s1set.remove(s1member);
			s2set.remove(s2member);
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
