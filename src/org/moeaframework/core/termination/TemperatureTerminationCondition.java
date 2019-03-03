package org.moeaframework.core.termination;

import org.moeaframework.algorithm.sa.AbstractSimulatedAnnealingAlgorithm;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.TerminationCondition;

public class TemperatureTerminationCondition implements TerminationCondition {

	@Override
	public void initialize(Algorithm algorithm) {
		// do nothing
		
	}

	@Override
	public boolean shouldTerminate(Algorithm algorithm) {
		if(algorithm instanceof AbstractSimulatedAnnealingAlgorithm) {
			if(!((AbstractSimulatedAnnealingAlgorithm) algorithm).isInitialized())
				return false;
			else
				return ((AbstractSimulatedAnnealingAlgorithm)algorithm).getTemperature() < ((AbstractSimulatedAnnealingAlgorithm)algorithm).gettMin();
		}
		return false;
	}

}
