package org.moeaframework.problem.RWMOP;

import org.moeaframework.core.Constraint;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.problem.AbstractProblem;

/**
 * Problem 2.1.5 - Disc Brake Design - from the RWMOP test problem suite.
 */
public class RCM05 extends AbstractProblem {

	public RCM05() {
		super(4, 2, 4);
	}

	@Override
	public void evaluate(Solution solution) {
		double x1 = EncodingUtils.getReal(solution.getVariable(0));
		double x2 = EncodingUtils.getReal(solution.getVariable(1));
		double x3 = EncodingUtils.getReal(solution.getVariable(2));
		double x4 = EncodingUtils.getReal(solution.getVariable(3));

		solution.setObjective(0, 4.9e-5 * (Math.pow(x2, 2.0) - Math.pow(x1, 2.0)) * (x4 - 1.0));
		solution.setObjective(1, 9.82e6 * (Math.pow(x2, 2.0) - Math.pow(x1, 2.0)) / (x3*x4*(Math.pow(x2, 3.0) - Math.pow(x1, 3.0))));
		
		solution.setConstraint(0, Constraint.lessThanOrEqual(20.0, x2 - x1));
		solution.setConstraint(1, Constraint.lessThanOrEqual(x3 / (3.14*(Math.pow(x2, 2.0) - Math.pow(x1, 2.0))), 0.4));
		solution.setConstraint(2, Constraint.lessThanOrEqual(2.22e-3 * x3 * (Math.pow(x2, 3.0) - Math.pow(x1, 3.0)) / Math.pow(Math.pow(x2, 2.0) - Math.pow(x1, 2.0), 2.0), 1.0));
		solution.setConstraint(3, Constraint.lessThanOrEqual(900.0, 2.66e-2*x3*x4*(Math.pow(x2, 3.0) - Math.pow(x1, 3.0)) / (Math.pow(x2, 2.0) - Math.pow(x1, 2.0))));
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(4, 2, 4);
		
		solution.setVariable(0, EncodingUtils.newReal(55.0, 80.0));
		solution.setVariable(1, EncodingUtils.newReal(75.0, 110.0));
		solution.setVariable(2, EncodingUtils.newReal(1000.0, 3000.0));
		solution.setVariable(3, EncodingUtils.newReal(11.0, 20.0));
		
		return solution;
	}

}
