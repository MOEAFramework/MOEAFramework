/* Copyright 2009-2018 David Hadka
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
package org.moeaframework.core.operator.permutation;

import java.util.Arrays;

import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variable;
import org.moeaframework.core.Variation;
import org.moeaframework.core.variable.Permutation;

/**
 * Partially mapped crossover (PMX) operator.  PMX is similar to two-point
 * crossover, but includes a repair operator to ensure the offspring are valid
 * permutations.
 * <p>
 * This variation operator is type-safe.
 * <p>
 * References:
 * <ol>
 * <li>Goldberg, D. and Lingle, R. Jr.
 * "Alleles, Loci, and the Traveling Salesman Problem." Proceedings of the 1st
 * International Conference on Genetic Algorithms and Their Applications. 1985.
 * </ol>
 */
public class PMX implements Variation {

	/**
	 * The probability of applying this operator.
	 */
	private final double probability;

	/**
	 * Constructs a PMX operator with the specified probability.
	 * 
	 * @param probability the probability of applying this operator
	 */
	public PMX(double probability) {
		super();
		this.probability = probability;
	}

	/*
	 * Modified from JMetal 3.0 by David Hadka on 18 September 2010. The
	 * original implementation in JMetal by Antonio J. Nebro is licensed under
	 * the GNU Lesser General Public License.
	 */
	/**
	 * Evolves the specified permutations using the PMX operator.
	 * 
	 * @param p1 the first permutation
	 * @param p2 the second permutation
	 * @throws VariationException if the permutations are not the same size
	 */
	public static void evolve(Permutation p1, Permutation p2) {
		int n = p1.size();

		if (n != p2.size()) {
			throw new FrameworkException("permutations not same size");
		}

		// select cutting points
		int cuttingPoint1 = PRNG.nextInt(n);
		int cuttingPoint2 = PRNG.nextInt(n - 1);

		if (cuttingPoint1 == cuttingPoint2) {
			cuttingPoint2 = n - 1;
		} else if (cuttingPoint1 > cuttingPoint2) {
			int swap = cuttingPoint1;
			cuttingPoint1 = cuttingPoint2;
			cuttingPoint2 = swap;
		}

		// exchange between the cutting points, setting up replacement arrays
		int[] parent1 = p1.toArray();
		int[] parent2 = p2.toArray();
		int[] offspring1 = new int[n];
		int[] offspring2 = new int[n];
		int[] replacement1 = new int[n];
		int[] replacement2 = new int[n];

		Arrays.fill(replacement1, -1);
		Arrays.fill(replacement2, -1);

		for (int i = cuttingPoint1; i <= cuttingPoint2; i++) {
			offspring1[i] = parent2[i];
			offspring2[i] = parent1[i];

			replacement1[parent2[i]] = parent1[i];
			replacement2[parent1[i]] = parent2[i];
		}

		// fill in remaining slots with replacements
		for (int i = 0; i < n; i++) {
			if ((i < cuttingPoint1) || (i > cuttingPoint2)) {
				int n1 = parent1[i];
				int m1 = replacement1[n1];

				int n2 = parent2[i];
				int m2 = replacement2[n2];

				while (m1 != -1) {
					n1 = m1;
					m1 = replacement1[m1];
				}

				while (m2 != -1) {
					n2 = m2;
					m2 = replacement2[m2];
				}

				offspring1[i] = n1;
				offspring2[i] = n2;
			}
		}

		p1.fromArray(offspring1);
		p2.fromArray(offspring2);
	}

	@Override
	public Solution[] evolve(Solution[] parents) {
		Solution result1 = parents[0].copy();
		Solution result2 = parents[1].copy();

		for (int i = 0; i < result1.getNumberOfVariables(); i++) {
			Variable variable1 = result1.getVariable(i);
			Variable variable2 = result2.getVariable(i);

			if ((PRNG.nextDouble() <= probability)
					&& (variable1 instanceof Permutation)
					&& (variable2 instanceof Permutation)) {
				evolve((Permutation)variable1, (Permutation)variable2);
			}
		}

		return new Solution[] { result1, result2 };
	}

	@Override
	public int getArity() {
		return 2;
	}

}
