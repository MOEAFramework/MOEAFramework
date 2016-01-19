package org.moeaframework.core.termination;

import org.moeaframework.core.Algorithm;
import org.moeaframework.core.TerminationCondition;

public class MaxFunctionEvaluations implements TerminationCondition {
	
	private int maxEvaluations;
	
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
