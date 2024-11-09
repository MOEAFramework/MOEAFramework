/* Copyright 2009-2024 David Hadka
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

import org.moeaframework.algorithm.Algorithm;

/**
 * Terminates a run when the maximum number of function evaluations is exceeded.
 */
public class MaxFunctionEvaluations implements TerminationCondition {
	
	/**
	 * The number of function evaluations at the start.
	 */
	private int startingEvaluations;
	
	/**
	 * The maximum number of function evaluations.
	 */
	private final int maxEvaluations;
	
	/**
	 * Constructs a new termination condition based on the maximum number of function evaluations.
	 * 
	 * @param maxEvaluations the maximum number of function evaluations
	 */
	public MaxFunctionEvaluations(int maxEvaluations) {
		super();
		this.maxEvaluations = maxEvaluations;
	}

	@Override
	public void initialize(Algorithm algorithm) {
		startingEvaluations = algorithm.getNumberOfEvaluations();
	}

	@Override
	public boolean shouldTerminate(Algorithm algorithm) {
		return (algorithm.getNumberOfEvaluations() - startingEvaluations) >= maxEvaluations;
	}
	
	@Override
	public double getPercentComplete(Algorithm algorithm) {
		return 100.0 * (algorithm.getNumberOfEvaluations() - startingEvaluations) / (double)maxEvaluations;
	}
	
	/**
	 * Attempts to determine the maximum number of function evaluations that would execute given the termination
	 * condition.
	 * 
	 * @param terminationCondition the termination condition
	 * @return the max number of function evaluations, or {@code -1} if the value could not be derived
	 */
	public static int derive(TerminationCondition terminationCondition) {
		if (terminationCondition instanceof MaxFunctionEvaluations maxFunctionEvaluations) {
			return maxFunctionEvaluations.maxEvaluations;
		}
		
		if (terminationCondition instanceof CompoundTerminationCondition compound) {
			for (TerminationCondition condition : compound) {
				int result = derive(condition);
				
				if (result >= 0) {
					return result;
				}
			}
		}
		
		return -1;
	}

}
