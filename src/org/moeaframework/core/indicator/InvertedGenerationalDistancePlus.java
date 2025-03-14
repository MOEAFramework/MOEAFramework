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
package org.moeaframework.core.indicator;

import org.moeaframework.core.Solution;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.problem.Problem;
import org.moeaframework.util.clustering.DistanceMeasure;

/**
 * Inverted generational distance plus (IGD+) indicator.  The "plus" variant differs in two ways:
 * <ol>
 *   <li>Utilizes a different distance measure to construct a weakly Pareto compliant indicator, and
 *   <li>Fixes the power to {@code 1.0} so the result is always the average distance.
 * </ol>
 * <p>
 * References:
 * <ol>
 *   <li>H. Ishibuchi, H. Masuda, Y. Tanigaki and Y. Nojima, "Modified distance calculation in generational distance
 *       and inverted generational distance," Proc. of 8th International Conference on Evolutionary Multi-Criterion
 *       Optimization, Part I, pp. 110-125, Guimarães, Portugal, March 29-April 1, 2015.
 * </ol>
 */
public class InvertedGenerationalDistancePlus extends NormalizedIndicator {
	
	/**
	 * The modified distance calculation used by the plus indicators as described in equation (18) in the cited paper.
	 * This is similar to the {@link GenerationalDistancePlus} version except the arguments are reversed.
	 */
	private static final DistanceMeasure<Solution> InvertedPlusDistanceMeasure = (referencePoint, approximationPoint) -> {
		double sum = 0.0;

		for (int i = 0; i < approximationPoint.getNumberOfObjectives(); i++) {
			sum += Math.pow(
					Math.max(approximationPoint.getObjectiveValue(i) - referencePoint.getObjectiveValue(i), 0.0),
					2.0);
		}

		return Math.sqrt(sum);
	};
	
	/**
	 * Constructs an inverted generational distance plus evaluator for the specified problem and corresponding
	 * reference set.  The default normalization procedure, as specified by {@link DefaultNormalizer}, is used.
	 * 
	 * @param problem the problem
	 * @param referenceSet the reference set for the problem
	 */
	public InvertedGenerationalDistancePlus(Problem problem, NondominatedPopulation referenceSet) {
		this(problem, referenceSet, null);
	}
	
	/**
	 * Constructs an inverted generational distance plus evaluator for the specified problem and corresponding
	 * reference set.
	 * 
	 * @param problem the problem
	 * @param referenceSet the reference set for the problem
	 * @param normalizer the user-provided normalizer, or {@code null} if the default is used
	 */
	public InvertedGenerationalDistancePlus(Problem problem, NondominatedPopulation referenceSet,
			Normalizer normalizer) {
		super(problem, referenceSet, normalizer);
	}

	@Override
	public double evaluate(NondominatedPopulation approximationSet) {
		return evaluate(problem, normalize(approximationSet), getNormalizedReferenceSet());
	}

	/**
	 * Computes the inverted generational distance plus for the specified problem given an approximation set and
	 * reference set.  While not necessary, the approximation and reference sets should be normalized.  Returns
	 * {@code Double.POSITIVE_INFINITY} if the approximation set is empty.
	 * 
	 * @param problem the problem
	 * @param approximationSet an approximation set for the problem
	 * @param referenceSet the reference set for the problem
	 * @return the inverted generational distance plus for the specified problem given an approximation set and
	 *         reference set
	 */
	static double evaluate(Problem problem, NondominatedPopulation approximationSet,
			NondominatedPopulation referenceSet) {
		double sum = 0.0;

		for (int i = 0; i < referenceSet.size(); i++) {
			sum += referenceSet.get(i).distanceToNearestSolution(approximationSet, InvertedPlusDistanceMeasure);
		}

		return sum / referenceSet.size();
	}
}
