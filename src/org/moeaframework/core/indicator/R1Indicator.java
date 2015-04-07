package org.moeaframework.core.indicator;

import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;

/**
 * Computes the R1 indicator.  The R1 indicator measures the fraction of
 * utility functions where the population is better than the reference set.
 * Values range from {@code [0, 1]} with {@code 1} preferred.  Note that to
 * achieve a value of {@code 1}, the population must be better than the
 * reference set with respect to all utility functions.
 * <p>
 * References:
 * <ol>
 *   <li>Hansen, M. P. and A. Jaszkiewicz (1998).  Evaluating the Quality of
 *       Approximations to the Non-dominated Set.  IMM Technical Report
 *       IMM-REP-1998-7.
 * </ol>
 */
public class R1Indicator extends RIndicator {

	/**
	 * Constructs a new R1 indicator using the Chebychev utility function.
	 * 
	 * @param problem the problem
	 * @param subdivisions the number of subdivisions along each objective
	 * @param referenceSet the reference set
	 */
	public R1Indicator(Problem problem, int subdivisions,
			NondominatedPopulation referenceSet) {
		this(problem, subdivisions, referenceSet, new ChebychevUtility());
	}
	
	/**
	 * Constructs a new R1 indicator using the specified utility function.
	 * 
	 * @param problem the problem
	 * @param subdivisions the number of subdivisions along each objective
	 * @param referenceSet the reference set
	 * @param utilityFunction the utility function
	 */
	public R1Indicator(Problem problem, int subdivisions,
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

			if (Math.abs(max2 - max1) < 0.00001) {
				sum += 0.5;
			} else if (max1 > max2) {
				sum += 1.0;
			}
		}
		
		return sum / weights.length;
	}

}
