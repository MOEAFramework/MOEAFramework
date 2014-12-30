/* Copyright 2009-2015 David Hadka
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

/**
 * Inverted generational distance indicator. Represents average distance from
 * solutions in the reference set to the nearest solution in an approximation
 * set.
 */
public class InvertedGenerationalDistance extends NormalizedIndicator {

	/**
	 * Constructs an inverted generational distance evaluator for the specified
	 * problem and corresponding reference set.
	 * 
	 * @param problem the problem
	 * @param referenceSet the reference set for the problem
	 */
	public InvertedGenerationalDistance(Problem problem,
			NondominatedPopulation referenceSet) {
		super(problem, referenceSet);
	}

	@Override
	public double evaluate(NondominatedPopulation approximationSet) {
		return evaluate(problem, normalize(approximationSet), 
				getNormalizedReferenceSet());
	}

	/**
	 * Computes the inverted generational distance for the specified problem
	 * given an approximation set and reference set. While not necessary, the
	 * approximation and reference sets should be normalized. Returns
	 * {@code Double.POSITIVE_INFINITY} if the approximation set is empty.
	 * 
	 * @param problem the problem
	 * @param approximationSet an approximation set for the problem
	 * @param referenceSet the reference set for the problem
	 * @return the inverted generational distance for the specified problem 
	 *         given an approximation set and reference set
	 */
	static double evaluate(Problem problem,
			NondominatedPopulation approximationSet,
			NondominatedPopulation referenceSet) {
		double sum = 0.0;

		for (int i = 0; i < referenceSet.size(); i++) {
			sum += Math.pow(IndicatorUtils.distanceToNearestSolution(problem,
					referenceSet.get(i), approximationSet), 2.0);
		}

		return Math.sqrt(sum) / referenceSet.size();
	}
}
