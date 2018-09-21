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
package org.moeaframework.analysis.sensitivity;

import org.moeaframework.core.Solution;
import org.moeaframework.problem.AbstractProblem;

/**
 * A problem stub with a defined number of objectives, but no decision
 * variables or constraints.  In addition, the {@link #evaluate(Solution)}
 * method throws {@link UnsupportedOperationException}.
 */
public class ProblemStub extends AbstractProblem {
	
	/**
	 * Constructs a problem stub with the specified number of objectives.
	 * 
	 * @param numberOfObjectives the number of objectives
	 */
	public ProblemStub(int numberOfObjectives) {
		super(0, numberOfObjectives, 0);
	}

	/**
	 * Throws {@code UnsupportedOperationException}.
	 * 
	 * @throws UnsupportedOperationException always
	 */
	@Override
	public void evaluate(Solution solution) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Solution newSolution() {
		return new Solution(numberOfVariables, numberOfObjectives, 
				numberOfConstraints);
	}

}
