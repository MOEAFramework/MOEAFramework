package org.moeaframework.core.indicator;

import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;

/**
 * Computes the R2 indicator.  The R2 indicator is the expected utility
 * evaluated across a set of uniformly-weighted utility functions.  R2 is
 * weakly compatible with the outperformance relation for any set of utility
 * functions.  Values are normalized between {@code [-1, 1]} with {@code -1}
 * preferred.
 * <p>
 * References:
 * <ol>
 *   <li>Hansen, M. P. and A. Jaszkiewicz (1998).  Evaluating the Quality of
 *       Approximations to the Non-dominated Set.  IMM Technical Report
 *       IMM-REP-1998-7.
 * </ol>
 */
public class R2Indicator extends RIndicator {

	/**
	 * Constructs a new R2 indicator using the Chebychev utility function.
	 * 
	 * @param problem the problem
	 * @param subdivisions the number of subdivisions along each objective
	 * @param referenceSet the reference set
	 */
	public R2Indicator(Problem problem, int subdivisions,
			NondominatedPopulation referenceSet) {
		this(problem, subdivisions, referenceSet, new ChebychevUtility());
	}
	
	/**
	 * Constructs a new R2 indicator using the specified utility function.
	 * 
	 * @param problem the problem
	 * @param subdivisions the number of subdivisions along each objective
	 * @param referenceSet the reference set
	 * @param utilityFunction the utility function
	 */
	public R2Indicator(Problem problem, int subdivisions,
			NondominatedPopulation referenceSet,
			UtilityFunction utilityFunction) {
		super(problem, subdivisions, referenceSet, utilityFunction);
	}
	
	@Override
	public double evaluate(NondominatedPopulation population) {
		return expectedUtility(
				getNormalizedReferenceSet()) - expectedUtility(population);
	}

}
