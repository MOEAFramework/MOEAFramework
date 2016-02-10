package org.moeaframework.core.termination;

import org.moeaframework.core.Algorithm;
import org.moeaframework.core.TerminationCondition;

/**
 * Terminates a run when one or more termination conditions are satisfied.
 */
public class CompoundTerminationCondition implements TerminationCondition {
	
	/**
	 * The termination conditions.
	 */
	private final TerminationCondition[] conditions;
	
	/**
	 * Constructs a new termination condition based on at least one individual
	 * termination condition being satsified.
	 * 
	 * @param conditions the termination conditions
	 */
	public CompoundTerminationCondition(TerminationCondition... conditions) {
		super();
		this.conditions = conditions;
	}

	@Override
	public void initialize(Algorithm algorithm) {
		for (TerminationCondition condition : conditions) {
			condition.initialize(algorithm);
		}
	}

	@Override
	public boolean shouldTerminate(Algorithm algorithm) {
		boolean shouldTerminate = false;
		
		for (TerminationCondition condition : conditions) {
			if (condition.shouldTerminate(algorithm)) {
				// do not break here in case an implementation needs to be
				// executed every step
				shouldTerminate = true;
			}
		}
		
		return shouldTerminate;
	}

}
