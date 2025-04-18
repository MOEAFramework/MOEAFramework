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

import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.problem.Problem;

/**
 * Computes the R2 indicator.  The R2 indicator is the expected utility evaluated across a set of uniformly-weighted
 * utility functions.  R2 is weakly compatible with the outperformance relation for any set of utility functions.
 * Values are normalized between {@code [-1, 1]} with {@code -1} preferred.
 * <p>
 * References:
 * <ol>
 *   <li>Hansen, M. P. and A. Jaszkiewicz (1998).  Evaluating the Quality of Approximations to the Non-dominated Set.
 *       IMM Technical Report IMM-REP-1998-7.
 * </ol>
 */
public class R2Indicator extends RIndicator {

	/**
	 * Constructs a new R2 indicator using the Chebyshev utility function.  The default normalization procedure, as
	 * specified by {@link DefaultNormalizer}, is used.
	 * 
	 * @param problem the problem
	 * @param subdivisions the number of subdivisions along each objective
	 * @param referenceSet the reference set
	 */
	public R2Indicator(Problem problem, int subdivisions, NondominatedPopulation referenceSet) {
		this(problem, subdivisions, referenceSet, null);
	}
	
	/**
	 * Constructs a new R2 indicator using the Chebyshev utility function.
	 * 
	 * @param problem the problem
	 * @param subdivisions the number of subdivisions along each objective
	 * @param referenceSet the reference set
	 * @param normalizer the user-provided normalizer, or {@code null} if the default is used
	 */
	public R2Indicator(Problem problem, int subdivisions, NondominatedPopulation referenceSet, Normalizer normalizer) {
		this(problem, subdivisions, referenceSet, normalizer, new ChebyshevUtility());
	}
	
	/**
	 * Constructs a new R2 indicator using the specified utility function.
	 * 
	 * @param problem the problem
	 * @param subdivisions the number of subdivisions along each objective
	 * @param referenceSet the reference set
	 * @param normalizer the user-provided normalizer, or {@code null} if the default is used
	 * @param utilityFunction the utility function
	 */
	public R2Indicator(Problem problem, int subdivisions, NondominatedPopulation referenceSet, Normalizer normalizer,
			UtilityFunction utilityFunction) {
		super(problem, subdivisions, referenceSet, normalizer, utilityFunction);
	}
	
	@Override
	public double evaluate(NondominatedPopulation population) {
		return expectedUtility(getNormalizedReferenceSet()) - expectedUtility(normalize(population));
	}

}
