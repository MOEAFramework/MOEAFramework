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
package org.moeaframework.problem;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.Subset;

/**
 * A mock problem with a subset variable.
 */
public class MockSubsetProblem extends MockProblem {
	
	public MockSubsetProblem() {
		this(1);
	}
	
	public MockSubsetProblem(int numberOfObjectives) {
		super(1, numberOfObjectives);
	}

	@Override
	public Solution newSolution() {
		Solution solution = super.newSolution();
		solution.setVariable(0, new Subset(5, 10));
		return solution;
	}

}
