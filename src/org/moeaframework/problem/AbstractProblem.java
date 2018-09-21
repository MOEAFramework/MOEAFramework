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

/**
 * Abstract class for a {@link Problem}.  For simplicity, most problems should
 * extend {@code AbstractProblem} rather than implement the {@code Problem}
 * interface directly.  At a minimum, only the {@link #newSolution()} and
 * {@link #evaluate(org.moeaframework.core.Solution)} methods need to be
 * defined.
 */
public abstract class AbstractProblem implements Problem {

	/**
	 * The number of variables defined by this problem.
	 */
	protected final int numberOfVariables;

	/**
	 * The number of objectives defined by this problem.
	 */
	protected final int numberOfObjectives;
	
	/**
	 * The number of constraints defined by this problem.
	 */
	protected final int numberOfConstraints;
	
	/**
	 * {@code true} if the {@code close()} method has been invoked; {@code 
	 * false} otherwise.
	 */
	private boolean isClosed;
	
	/**
	 * Constructs an unconstrained abstract problem with the specified number
	 * of decision variables and objectives.
	 * 
	 * @param numberOfVariables the number of decision variables
	 * @param numberOfObjectives the number of objectives
	 */
	public AbstractProblem(int numberOfVariables, int numberOfObjectives) {
		this(numberOfVariables, numberOfObjectives, 0);
	}
	
	/**
	 * Constructs an abstract problem with the specified number of decision 
	 * variables, objectives and constraints.
	 * 
	 * @param numberOfVariables the number of decision variables
	 * @param numberOfObjectives the number of objectives
	 * @param numberOfConstraints the number of constraints
	 */
	public AbstractProblem(int numberOfVariables, int numberOfObjectives, 
			int numberOfConstraints) {
		super();
		this.numberOfVariables = numberOfVariables;
		this.numberOfObjectives = numberOfObjectives;
		this.numberOfConstraints = numberOfConstraints;
	}
	
	@Override
	public String getName() {
		return getClass().getSimpleName();
	}

	@Override
	public int getNumberOfVariables() {
		return numberOfVariables;
	}

	@Override
	public int getNumberOfObjectives() {
		return numberOfObjectives;
	}

	@Override
	public int getNumberOfConstraints() {
		return numberOfConstraints;
	}

	/**
	 * Calls {@code close()} if this problem has not yet been closed prior to
	 * finalization.
	 */
	@Override
	protected void finalize() throws Throwable {
		if (!isClosed) {
			close();
		}
		
		super.finalize();
	}

	@Override
	public void close() {
		isClosed = true;
	}

}
