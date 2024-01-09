package org.moeaframework.analysis.sensitivity;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Settings;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.AbstractProblem;
import org.moeaframework.problem.MockRealProblem;

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
	public void testRandomlyFeasibleSolutions() {
		double percentFeasible = 0.25;
		
		Problem problem = new AbstractProblem(1, 1, 1) {

			@Override
			public void evaluate(Solution solution) {
				solution.setConstraint(0, PRNG.nextDouble() < percentFeasible ? 0.0 : -1.0);
			}

			@Override
			public Solution newSolution() {
				Solution solution = new Solution(1, 1, 1);
				solution.setVariable(0, new RealVariable(0.0, 1.0));
				return solution;
			}
			
		};
		
		Assert.assertEquals(percentFeasible, FeasibilityRatio.calculate(problem, 10000), 0.01);
	}

}
