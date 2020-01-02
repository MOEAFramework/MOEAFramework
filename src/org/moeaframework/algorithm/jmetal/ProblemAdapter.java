/* Copyright 2009-2019 David Hadka
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
package org.moeaframework.algorithm.jmetal;

import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;

public abstract class ProblemAdapter<T extends org.uma.jmetal.solution.Solution<?>> implements org.uma.jmetal.problem.Problem<T> {

	private static final long serialVersionUID = 5625585375846735318L;
	
	private final Problem problem;
	
	public ProblemAdapter(Problem problem) {
		this.problem = problem;
	}
	
	public Problem getProblem() {
		return problem;
	}
	
	@Override
	public String getName() {
		return problem.getName();
	}
	
	@Override
	public int getNumberOfConstraints() {
		return problem.getNumberOfConstraints();
	}

	@Override
	public int getNumberOfObjectives() {
		return problem.getNumberOfObjectives();
	}
	
	@Override
	public int getNumberOfVariables() {
		return problem.getNumberOfVariables();
	}
	
	public abstract Solution convert(T solution);
	
	@Override
	public void evaluate(T solution) {
		Solution result = convert(solution);

		getProblem().evaluate(result);
		
		JMetalUtils.copyObjectivesAndConstraints(result, solution);
	}

	public int getNumberOfMutationIndices() {
		return getNumberOfVariables();
	}
	
}
