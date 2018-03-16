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
 * Computes the R3 indicator.  The R3 indicator is a utility ratio, or the
 * value of the R2 indicator divided by the reference set utility.  Values
 * range from {@code [-inf, inf]} with values nearer to {@code -inf} preferred.
 * <p>
 * References:
 * <ol>
 *   <li>Hansen, M. P. and A. Jaszkiewicz (1998).  Evaluating the Quality of
 *       Approximations to the Non-dominated Set.  IMM Technical Report
 *       IMM-REP-1998-7.
 * </ol>
 */
public class R3Indicator extends RIndicator {

	/**
	 * Constructs a new R3 indicator using the Chebychev utility function.
	 * 
	 * @param problem the problem
	 * @param subdivisions the number of subdivisions along each objective
	 * @param referenceSet the reference set
	 */
	public R3Indicator(Problem problem, int subdivisions,
			NondominatedPopulation referenceSet) {
		this(problem, subdivisions, referenceSet, new ChebychevUtility());
	}
	
	/**
	 * Constructs a new R3 indicator using the specified utility function.
	 * 
	 * @param problem the problem
	 * @param subdivisions the number of subdivisions along each objective
	 * @param referenceSet the reference set
	 * @param utilityFunction the utility function
	 */
	public R3Indicator(Problem problem, int subdivisions,
			NondominatedPopulation referenceSet,
			UtilityFunction utilityFunction) {
		super(problem, subdivisions, referenceSet, utilityFunction);
	}
	
	@Override
	public double evaluate(NondominatedPopulation population) {
		double sum = 0.0;
		
		for (int i = 0; i < weights.length; i++) {
			double max1 = Double.NEGATIVE_INFINITY;
			double max2 = Double.NEGATIVE_INFINITY;
			
			for (Solution solution : population) {
				max1 = Math.max(max1, utilityFunction.computeUtility(solution,
						weights[i]));
			}
			
			for (Solution solution : getNormalizedReferenceSet()) {
				max2 = Math.max(max2, utilityFunction.computeUtility(solution,
						weights[i]));
			}
			
			sum += (max2 - max1) / (max2 + 1e-30);
		}
		
		return sum / weights.length;
	}

}
