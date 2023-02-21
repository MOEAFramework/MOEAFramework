package org.moeaframework.core.indicator;

import java.util.Arrays;

import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;

/**
 * Fast hypervolume calculation published by the Walking Fish Group (WFG).  This version is normalized by the bounds of
 * a reference set.
 * <p>
 * References:
 * <ol>
 *   <li>While, Ronald Lyndon et al. “A Fast Way of Calculating Exact Hypervolumes.” IEEE Transactions on Evolutionary
 *       Computation 16 (2012): 86-95.
 * </ol>
 */
public class WFGNormalizedHypervolume extends NormalizedIndicator {
	
	/**
	 * The unnormalized hypervolume calculation.
	 */
	private WFGHypervolume hypervolume;

	/**
	 * Constructs a hypervolume evaluator for the specified problem and 
	 * reference set.
	 * 
	 * @param problem the problem
	 * @param referenceSet the reference set
	 */
	public WFGNormalizedHypervolume(Problem problem, NondominatedPopulation referenceSet) {
		super(problem, referenceSet, true);
		this.hypervolume = new WFGHypervolume(problem, getNormalizedReferencePoint(problem));
	}
	
	/**
	 * Constructs a hypervolume evaluator for the specified problem using the
	 * given reference set and reference point.
	 * 
	 * @param problem the problem
	 * @param referenceSet the reference set
	 * @param referencePoint the reference point
	 */
	public WFGNormalizedHypervolume(Problem problem, NondominatedPopulation referenceSet, double[] referencePoint) {
		super(problem, referenceSet, referencePoint);
		this.hypervolume = new WFGHypervolume(problem, getNormalizedReferencePoint(problem));
	}
	
	/**
	 * Constructs a hypervolume evaluator for the specified problem using the
	 * given minimum and maximum bounds.
	 * 
	 * @param problem the problem
	 * @param minimum the minimum bounds of the set
	 * @param maximum the maximum bounds of the set
	 */
	public WFGNormalizedHypervolume(Problem problem, double[] minimum, double[] maximum) {
		super(problem, new NondominatedPopulation(), minimum, maximum);
		this.hypervolume = new WFGHypervolume(problem, getNormalizedReferencePoint(problem));
	}
	
	/**
	 * Creates the reference point to be used after normalization, i.e., {@code [1, ..., 1]}.
	 * 
	 * @param problem the problem
	 * @return the reference point
	 */
	private static final double[] getNormalizedReferencePoint(Problem problem) {
		double[] referencePoint = new double[problem.getNumberOfObjectives()];
		Arrays.fill(referencePoint, 1.0);
		return referencePoint;
	}

	@Override
	public double evaluate(NondominatedPopulation approximationSet) {
		return hypervolume.evaluate(normalize(approximationSet));
	}
	
	/**
	 * Computes the hypervolume of the normalized approximation set.
	 * 
	 * @param problem the problem
	 * @param approximationSet the normalized approximation set
	 * @return the hypervolume of the normalized approximation set
	 */
	static double evaluate(Problem problem,
			NondominatedPopulation approximationSet) {
		return new WFGHypervolume(problem, getNormalizedReferencePoint(problem)).evaluate(approximationSet);
	}

}
