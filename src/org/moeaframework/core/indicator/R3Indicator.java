package org.moeaframework.core.indicator;

import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;

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
		double referenceSetUtility = expectedUtility(
				getNormalizedReferenceSet());
		
		return (referenceSetUtility - expectedUtility(population)) /
				(referenceSetUtility + 1e-30);
	}

}
