package org.moeaframework.core.termination;

import org.moeaframework.core.Algorithm;
import org.moeaframework.core.TerminationCondition;

/**
 * Terminates a run when the maximum number of function evaluations is
 * exceeded.
 */
public class MaxFunctionEvaluations implements TerminationCondition {
	
	/**
	 * The maximum number of function evaluations.
	 */
	private int maxEvaluations;
	
	/**
	 * Constructs a new termination condition based on the maximum number of
	 * function evaluations.
	 * 
	 * @param maxEvaluations the maximum number of function evaluations
	 */
	public MaxFunctionEvaluations(int maxEvaluations) {
		super();
		this.maxEvaluations = maxEvaluations;
	}

	@Override
	public void initialize(Algorithm algorithm) {
		// do nothing
	}

	@Override
	public boolean shouldTerminate(Algorithm algorithm) {
		return algorithm.getNumberOfEvaluations() >= maxEvaluations;
	}

}
