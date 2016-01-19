package org.moeaframework.core.termination;

import org.moeaframework.core.Algorithm;
import org.moeaframework.core.TerminationCondition;

public class MaxElapsedTime implements TerminationCondition {
	
	private final long maxTime;
	
	private long startTime;
	
	public MaxElapsedTime(long maxTime) {
		super();
		this.maxTime = maxTime;
	}

	@Override
	public void initialize(Algorithm algorithm) {
		startTime = System.currentTimeMillis();
	}

	@Override
	public boolean shouldTerminate(Algorithm algorithm) {
		return (System.currentTimeMillis() - startTime) >= maxTime;
	}

}
