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
package org.moeaframework.mock;

import org.junit.Assert;
import org.moeaframework.core.Solution;
import org.moeaframework.problem.AbstractProblem;

public class MockProblem extends AbstractProblem {
	
	private final Solution prototype;
		
	public MockProblem(int numberOfObjectives) {
		this(0, numberOfObjectives);
	}
	
	public MockProblem(int numberOfVariables, int numberOfObjectives) {
		this(numberOfVariables, numberOfObjectives, 0);
	}

	public MockProblem(int numberOfVariables, int numberOfObjectives, int numberOfConstraints) {
		this(new Solution(numberOfVariables, numberOfObjectives, numberOfConstraints));
	}
	
	public MockProblem(Solution prototype) {
		super(prototype.getNumberOfVariables(), prototype.getNumberOfObjectives(), prototype.getNumberOfConstraints());
		this.prototype = prototype;
	}
	
	@Override
	public void evaluate(Solution solution) {
		if (solution instanceof MockSolution mockSolution && mockSolution.getNumberOfObjectives() > 0) {
			// When given a MockSolution with results, simply leave it as-is.
		} else if (solution.getNumberOfVariables() > 0) {
			// When given a solution with at least one variable, derive the objective values from the variable hash.
			// This ensures resulting values are distinct but also deterministic.
			int value = solution.getVariable(0).hashCode();
					
			for (int i = 0; i < getNumberOfObjectives(); i++) {
				solution.setObjectiveValue(i, i % 2 == 0 ? value : Integer.MAX_VALUE - value);
			}
		} else {
			Assert.fail("MockProblem must either be supplied with a MockSolution or at least one decision variable");
		}
	}

	@Override
	public Solution newSolution() {
		return prototype.copy();
	}
	
	public static MockProblem of(Solution solution) {
		return new MockProblem(solution);
	}

}
