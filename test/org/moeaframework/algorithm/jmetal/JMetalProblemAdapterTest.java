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

import jmetal.util.JMException;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.AbstractProblem;

/**
 * Tests the {@link JMetalProblemAdapter} class.
 */
public class JMetalProblemAdapterTest {
	
	/**
	 * Mock class for a problem with constraint violations.
	 */
	private class MockConstraintProblem extends AbstractProblem {

		public MockConstraintProblem() {
			super(1, 1, 3);
		}

		@Override
		public void evaluate(Solution solution) {
			solution.setConstraint(0, -15.0);
			solution.setConstraint(1, 0.0);
			solution.setConstraint(2, 20.0);
		}

		@Override
		public Solution newSolution() {
			Solution solution = new Solution(1, 1, 3);
			solution.setVariable(0, new RealVariable(0.0, 1.0));
			return solution;
		}
		
	}
	
	/**
	 * Tests to ensure that the aggregate constraint violation sent to JMetal
	 * is correct.  In JMetal, constraint violations are indicated by
	 * negative values.
	 * 
	 * @throws ClassNotFoundException should not occur
	 * @throws JMException should not occur
	 */
	@Test
	public void testConstraintAggregation() throws ClassNotFoundException,
	JMException {
		MockConstraintProblem problem = new MockConstraintProblem();
		JMetalProblemAdapter adapter = new JMetalProblemAdapter(problem);
		
		jmetal.core.Solution solution = new jmetal.core.Solution(adapter);
		adapter.evaluate(solution);
		
		Assert.assertEquals(-35.0, solution.getOverallConstraintViolation(),
				Settings.EPS);
		Assert.assertEquals(2, solution.getNumberOfViolatedConstraint());
	}

}
