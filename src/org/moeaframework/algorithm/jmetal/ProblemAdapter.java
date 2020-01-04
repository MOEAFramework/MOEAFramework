/* Copyright 2009-2020 David Hadka
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

/**
 * Base class that facilitates passing MOEA Framework problems to JMetal.
 * 
 * @param <T> The type of the JMetal solution
 */
public abstract class ProblemAdapter<T extends org.uma.jmetal.solution.Solution<?>>
implements org.uma.jmetal.problem.Problem<T> {

	private static final long serialVersionUID = 5625585375846735318L;
	
	/**
	 * The MOEA Framework problem.
	 */
	protected final Problem problem;
	
	/**
	 * The problem schema, which defines the variable types and bounds.
	 */
	protected final Solution schema;
	
	/**
	 * Creates a new problem adapter for the given MOEA Framework problem.
	 * 
	 * @param problem the MOEA Framework problem
	 */
	public ProblemAdapter(Problem problem) {
		this.problem = problem;
		this.schema = problem.newSolution();
	}
	
	/**
	 * Returns the MOEA Framework problem.
	 * 
	 * @return the MOEA Framework problem
	 */
	public Problem getProblem() {
		return problem;
	}
	
	/**
	 * Returns the problem schema, which defines the variable types and bounds.
	 * 
	 * @return the problem schema, which defines the variable types and bounds
	 */
	public Solution getSchema() {
		return schema;
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
	
	/**
	 * Converts the given JMetal solution back into a MOEA Framework solution.
	 * 
	 * @param solution the JMetal solution
	 * @return the equivalent MOEA Framework solution
	 */
	public abstract Solution convert(T solution);
	
	@Override
	public void evaluate(T solution) {
		Solution result = convert(solution);

		getProblem().evaluate(result);
		
		JMetalUtils.copyObjectivesAndConstraints(result, solution);
	}

	/**
	 * Returns the number of decision variables eligible for mutation.  This is
	 * used to compute mutation rates.
	 * 
	 * @return the number of decision variables eligible for mutation
	 */
	public int getNumberOfMutationIndices() {
		return getNumberOfVariables();
	}
	
}
