/* Copyright 2009-2025 David Hadka
 *
 * This file is part of the MOEA Framework.
 *
 * The MOEA Framework is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * The MOEA Framework is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the MOEA Framework.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.moeaframework.core.termination;

import java.util.Iterator;

import org.moeaframework.algorithm.Algorithm;
import org.moeaframework.util.Iterators;

/**
 * Terminates a run when one or more termination conditions are satisfied.
 */
public class CompoundTerminationCondition implements TerminationCondition, Iterable<TerminationCondition> {
	
	/**
	 * The termination conditions.
	 */
	private final TerminationCondition[] conditions;
	
	/**
	 * Constructs a new termination condition based on at least one individual termination condition being satisfied.
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
				// do not break here in case an implementation needs to be executed every step
				shouldTerminate = true;
			}
		}
		
		return shouldTerminate;
	}
	
	@Override
	public double getPercentComplete(Algorithm algorithm) {
		double overallPercentComplete = 0.0;
		
		for (TerminationCondition condition : conditions) {
			double percentComplete = condition.getPercentComplete(algorithm);
			
			if (!Double.isNaN(percentComplete)) {
				overallPercentComplete = Math.max(percentComplete, overallPercentComplete);
			}
		}
		
		return overallPercentComplete;
	}

	@Override
	public Iterator<TerminationCondition> iterator() {
		return Iterators.of(conditions);
	}

}
