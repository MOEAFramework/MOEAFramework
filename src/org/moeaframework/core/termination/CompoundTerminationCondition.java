package org.moeaframework.core.termination;

import org.moeaframework.core.Algorithm;
import org.moeaframework.core.TerminationCondition;

public class CompoundTerminationCondition implements TerminationCondition {
	
	private final TerminationCondition[] conditions;
	
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
