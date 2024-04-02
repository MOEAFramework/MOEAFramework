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
package org.moeaframework.analysis;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.mock.MockRealProblem;
import org.moeaframework.problem.AbstractProblem;

public class FeasibilityRatioTest {
	
	@Test
	public void testNoConstraints() {
		Assert.assertEquals(1.0, FeasibilityRatio.calculate(new MockRealProblem(2), 10), Settings.EPS);
	}
	
	@Test
	public void testAllFeasibleSolutions() {
		Problem problem = new AbstractProblem(1, 1, 1) {

			@Override
			public void evaluate(Solution solution) {
				solution.setConstraint(0, 0.0);
			}

			@Override
			public Solution newSolution() {
				Solution solution = new Solution(1, 1, 1);
				solution.setVariable(0, new RealVariable(0.0, 1.0));
				return solution;
			}
			
		};
		
		Assert.assertEquals(1.0, FeasibilityRatio.calculate(problem, 10), Settings.EPS);
	}
	
	@Test
	public void testNoFeasibleSolutions() {
		Problem problem = new AbstractProblem(1, 1, 1) {

			@Override
			public void evaluate(Solution solution) {
				solution.setConstraint(0, -1.0);
			}

			@Override
			public Solution newSolution() {
				Solution solution = new Solution(1, 1, 1);
				solution.setVariable(0, new RealVariable(0.0, 1.0));
				return solution;
			}
			
		};
		
		Assert.assertEquals(0.0, FeasibilityRatio.calculate(problem, 10), Settings.EPS);
	}
	
	@Test
	public void testPercentFeasibleSolutions() {
		Problem problem = new AbstractProblem(1, 1, 1) {
			
			private int count = 0;

			@Override
			public void evaluate(Solution solution) {
				solution.setConstraint(0, (count++) % 4 == 0 ? 0.0 : -1.0);
			}

			@Override
			public Solution newSolution() {
				Solution solution = new Solution(1, 1, 1);
				solution.setVariable(0, new RealVariable(0.0, 1.0));
				return solution;
			}
			
		};
		
		Assert.assertEquals(0.25, FeasibilityRatio.calculate(problem, 100), 0.01);
	}

}
