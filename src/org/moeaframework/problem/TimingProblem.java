/* Copyright 2009-2018 David Hadka
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
package org.moeaframework.problem;

import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;

/**
 * Decorates a problem to collect objective function evaluation timing data.
 * Precision and accuracy of the timing data is dependent on {@link 
 * System#nanoTime()}.
 */
public class TimingProblem implements Problem {
	
	/**
	 * The decorated problem.
	 */
	private final Problem problem;
	
	/**
	 * The time, in seconds, expended on objective function evaluation.
	 */
	private double time;
	
	/**
	 * Decorates the specified problem to collecting objective function
	 * evaluation timing data.
	 * 
	 * @param problem the problem to decorate
	 */
	public TimingProblem(Problem problem) {
		super();
		this.problem = problem;
	}

	@Override
	public String getName() {
		return problem.getName();
	}

	@Override
	public int getNumberOfVariables() {
		return problem.getNumberOfVariables();
	}

	@Override
	public int getNumberOfObjectives() {
		return problem.getNumberOfObjectives();
	}

	@Override
	public int getNumberOfConstraints() {
		return problem.getNumberOfConstraints();
	}

	@Override
	public void evaluate(Solution solution) {
		long start = System.nanoTime();
		problem.evaluate(solution);
		long end = System.nanoTime();
		
		time += (end - start) / 1e9;
	}

	@Override
	public Solution newSolution() {
		return problem.newSolution();
	}
	
	/**
	 * Clears any timing data collected.  
	 */
	public void clear() {
		time = 0.0;
	}
	
	/**
	 * Returns the time, in seconds, expended on objective function evaluation.
	 * 
	 * @return the time, in seconds, expended on objective function evaluation
	 */
	public double getTime() {
		return time;
	}

	@Override
	public void close() {
		problem.close();
	}

}
