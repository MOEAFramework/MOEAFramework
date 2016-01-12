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
package org.moeaframework.core.indicator;

import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Settings;

/**
 * Generational distance indicator. Represents average distance from solutions
 * in an approximation set to the nearest solution in the reference set.
 */
public class GenerationalDistance extends NormalizedIndicator {
	
	/**
	 * Set to {@code 2.0} to replicate generational distance as seen in the
	 * literature, {@code 1.0} to compute the average distance, or any other
	 * power.
	 */
	private final double d;

	/**
	 * Constructs a generational distance evaluator for the specified problem 
	 * and corresponding reference set.
	 * 
	 * @param problem the problem
	 * @param referenceSet the reference set for the problem
	 */
	public GenerationalDistance(Problem problem,
			NondominatedPopulation referenceSet) {
		this(problem, referenceSet, Settings.getGDPower());
	}
	
	/**
	 * Constructs a generational distance evaluator for the specified problem 
	 * and corresponding reference set.
	 * 
	 * @param problem the problem
	 * @param referenceSet the reference set for the problem
	 * @param d the power, typically {@code 2.0}
	 */
	public GenerationalDistance(Problem problem,
			NondominatedPopulation referenceSet,
			double d) {
		super(problem, referenceSet);
		this.d = d;
	}

	@Override
	public double evaluate(NondominatedPopulation approximationSet) {
		return evaluate(problem, normalize(approximationSet), 
				getNormalizedReferenceSet(), d);
	}

	/**
	 * Computes the generational distance for the specified problem given an
	 * approximation set and reference set. While not necessary, the
	 * approximation and reference sets should be normalized. Returns
	 * {@code Double.POSITIVE_INFINITY} if the approximation set is empty.
	 * 
	 * @param problem the problem
	 * @param approximationSet an approximation set for the problem
	 * @param referenceSet the reference set for the problem
	 * @param d the power, typically {@code 2.0}
	 * @return the generational distance for the specified problem given an
	 *         approximation set and reference set
	 */
	static double evaluate(Problem problem,
			NondominatedPopulation approximationSet,
			NondominatedPopulation referenceSet,
			double d) {
		double sum = 0.0;
		
		if (approximationSet.isEmpty()) {
			return Double.POSITIVE_INFINITY;
		}

		for (int i = 0; i < approximationSet.size(); i++) {
			sum += Math.pow(IndicatorUtils.distanceToNearestSolution(problem,
					approximationSet.get(i), referenceSet), d);
		}
		
		return Math.pow(sum, 1.0 / d) / approximationSet.size();
	}
}
