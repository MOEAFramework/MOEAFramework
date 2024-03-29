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

import org.moeaframework.core.Algorithm;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.TerminationCondition;

/**
 * Terminates a run when at least one solution in the result is equal to or within some allowed difference (epsilon)
 * from a target solution.
 */
public class TargetObjectiveTermination implements TerminationCondition {
	
	/**
	 * The target solution.
	 */
	private final Solution target;
	
	/**
	 * The allowed difference or distance between the solution objectives and target.
	 */
	private final double epsilon;
	
	/**
	 * Constructs a new termination condition using distance to target objective values.
	 * 
	 * @param target the target objective values
	 */
	public TargetObjectiveTermination(double[] target) {
		this(target, Settings.EPS);
	}
	
	/**
	 * Constructs a new termination condition using distance to target objective values.
	 * 
	 * @param target the target objective values
	 * @param epsilon the allowed error
	 */
	public TargetObjectiveTermination(double[] target, double epsilon) {
		this(toSolution(target), epsilon);
	}
	
	/**
	 * Constructs a new termination condition using distance to a target solution.
	 * 
	 * @param target the target solution
	 */
	public TargetObjectiveTermination(Solution target) {
		this(target, Settings.EPS);
	}
	
	/**
	 * Constructs a new termination condition using distance to a target solution.
	 * 
	 * @param target the target solution
	 * @param epsilon the allowed error
	 */
	public TargetObjectiveTermination(Solution target, double epsilon) {
		super();
		this.target = target;
		this.epsilon = epsilon;
	}

	@Override
	public void initialize(Algorithm algorithm) {
		
	}

	@Override
	public boolean shouldTerminate(Algorithm algorithm) {
		NondominatedPopulation result = algorithm.getResult();
		
		for (Solution solution : result) {
			if (solution.euclideanDistance(target) <= epsilon) {
				return true;
			}
		}
		
		return false;
	}
	
	private static final Solution toSolution(double[] objectives) {
		Solution solution = new Solution(0, objectives.length);
		solution.setObjectives(objectives);
		return solution;
	}

}
