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
package org.moeaframework.core.indicator;

import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;

/**
 * Additive &epsilon;-indicator for minimization problems. Finds the minimum
 * &epsilon; value for the approximation set to &epsilon;-dominate the reference
 * set.
 */
public class AdditiveEpsilonIndicator extends NormalizedIndicator {

	/**
	 * Constructs an additive &epsilon;-indicator evaluator for the specified 
	 * problem and corresponding reference set.
	 * 
	 * @param problem the problem
	 * @param referenceSet the reference set for the problem
	 */
	public AdditiveEpsilonIndicator(Problem problem,
			NondominatedPopulation referenceSet) {
		super(problem, referenceSet);
	}

	@Override
	public double evaluate(NondominatedPopulation approximationSet) {
		return evaluate(problem, normalize(approximationSet), 
				getNormalizedReferenceSet());
	}

	/**
	 * Computes the additive &epsilon;-indicator for the specified problem given
	 * an approximation set and reference set.  While not necessary, the
	 * approximation and reference sets should be normalized.  Returns
	 * {@code Double.POSITIVE_INFINITY} if the approximation set is empty.
	 * 
	 * @param problem the problem
	 * @param approximationSet an approximation set for the problem
	 * @param referenceSet the reference set for the problem
	 * @return the additive &epsilon;-indicator value for the specified problem
	 *         given an approximation set and reference set
	 */
	static double evaluate(Problem problem,
			NondominatedPopulation approximationSet,
			NondominatedPopulation referenceSet) {
		double eps_i = 0.0;

		for (int i = 0; i < referenceSet.size(); i++) {
			Solution solution1 = referenceSet.get(i);
			double eps_j = Double.POSITIVE_INFINITY;

			for (int j = 0; j < approximationSet.size(); j++) {
				Solution solution2 = approximationSet.get(j);
				double eps_k = 0.0;

				for (int k = 0; k < problem.getNumberOfObjectives(); k++) {
					eps_k = Math.max(eps_k, solution2.getObjective(k)
							- solution1.getObjective(k));
				}

				eps_j = Math.min(eps_j, eps_k);
			}

			eps_i = Math.max(eps_i, eps_j);
		}

		return eps_i;
	}

}
