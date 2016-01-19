package org.moeaframework.core;

/**
 * Interface used to implement conditions for when an algorithm should
 * terminate.  The {@code initialize} method is invoked when the algorithm is
 * first created to collect any initial conditions, such as the starting time,
 * and {@code shouldTerminate} is invoked every step to check if the algorithm
 * should terminate.
 */
public interface TerminationCondition {
	
	/**
	 * Invoked when the algorithm is created to collect any initial
	 * conditions.  Note that the algorithm may not have been initialized at
	 * this point.
	 * 
	 * @param algorithm the algorithm
	 */
	public void initialize(Algorithm algorithm);
	
	/**
	 * Invoked after every step to check if the algorithm should terminate.
	 * 
	 * @param algorithm the algorithm
	 * @return {@code true} if the algorithm should terminate; {@code false}
	 *         otherwise
	 */
	public boolean shouldTerminate(Algorithm algorithm); 

}
