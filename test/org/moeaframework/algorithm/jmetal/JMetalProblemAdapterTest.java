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
		
		jmetal.base.Solution solution = new jmetal.base.Solution(adapter);
		adapter.evaluate(solution);
		
		Assert.assertEquals(-35.0, solution.getOverallConstraintViolation(),
				Settings.EPS);
		Assert.assertEquals(2, solution.getNumberOfViolatedConstraint());
	}

}
