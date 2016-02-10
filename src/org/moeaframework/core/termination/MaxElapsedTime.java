package org.moeaframework.core.termination;

import org.moeaframework.core.Algorithm;
import org.moeaframework.core.TerminationCondition;

/**
 * Terminates a run when the maximum elapsed time is exceeded.
 */
public class MaxElapsedTime implements TerminationCondition {
	
	/**
	 * The maximum elapsed time in milliseconds.
	 */
	private final long maxTime;
	
	/**
	 * The starting time in milliseconds.
	 */
	private long startTime;
	
	/**
	 * Constructs a new termination condition based on the maximum elapsed time.
	 * 
	 * @param maxTime the maximum elapsed time in milliseconds
	 */
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
