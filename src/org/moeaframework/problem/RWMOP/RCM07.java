package org.moeaframework.problem.RWMOP;

import org.moeaframework.core.Constraint;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.problem.AbstractProblem;

/**
 * Problem 2.1.7 - Gear Train Design - from the RWMOP test problem suite.
 */
public class RCM07 extends AbstractProblem {

	public RCM07() {
		super(4, 2, 1);
	}

	@Override
	public void evaluate(Solution solution) {
		int x1 = EncodingUtils.getInt(solution.getVariable(0));
		int x2 = EncodingUtils.getInt(solution.getVariable(1));
		int x3 = EncodingUtils.getInt(solution.getVariable(2));
		int x4 = EncodingUtils.getInt(solution.getVariable(3));

		solution.setObjective(0, Math.abs(6.931 - (x3*x4)/(x1*x2)));
		solution.setObjective(1, Math.max(x1, Math.max(x2, Math.max(x3, x4))));

		solution.setConstraint(0, Constraint.lessThanOrEqual(solution.getObjective(0) / 6.931, 0.5));
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(4, 2, 1);
		
		solution.setVariable(0, EncodingUtils.newInt(12, 60));
		solution.setVariable(1, EncodingUtils.newInt(12, 60));
		solution.setVariable(2, EncodingUtils.newInt(12, 60));
		solution.setVariable(3, EncodingUtils.newInt(12, 60));
		
		return solution;
	}

}
