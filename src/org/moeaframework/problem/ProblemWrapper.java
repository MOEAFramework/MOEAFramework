/* Copyright 2009-2022 David Hadka
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
 * Wraps a problem instance to modify or extend its functionality.  All methods
 * call the corresponding method on the wrapped problem, unless overridden by
 * a subclass.
 */
public abstract class ProblemWrapper implements Problem {

	/**
	 * The original problem instance.
	 */
	protected final Problem problem;
	
	/**
	 * Constructs a new problem wrapper to modify or extend the functionality of the
	 * given problem.
	 * 
	 * @param problem the problem being modified or extended
	 */
	protected ProblemWrapper(Problem problem) {
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
		problem.evaluate(solution);
	}

	@Override
	public Solution newSolution() {
		return problem.newSolution();
	}

	@Override
	public void close() {
		problem.close();
	}

}
