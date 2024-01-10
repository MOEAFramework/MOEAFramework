package org.moeaframework.problem.RWMOP;

import org.moeaframework.core.Constraint;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.problem.AbstractProblem;

/**
 * Problem 2.1.3 - Two Bar Truss Design - from the RWMOP test problem suite.
 */
public class RCM03 extends AbstractProblem {

	public RCM03() {
		super(3, 2, 3);
	}

	@Override
	public void evaluate(Solution solution) {
		double x1 = EncodingUtils.getReal(solution.getVariable(0));
		double x2 = EncodingUtils.getReal(solution.getVariable(1));
		double x3 = EncodingUtils.getReal(solution.getVariable(2));
		
		double f1 = x1*Math.sqrt((16.0 + x3*x3)) + x2*Math.sqrt(1.0 + x3*x3);
		double f2 = 20.0*Math.sqrt(16.0 + x3*x3) / (x3*x1);
		
		solution.setObjective(0, f1);
		solution.setObjective(1, f2);
		
		solution.setConstraint(0, Constraint.lessThanOrEqual(f1, 0.1));
		solution.setConstraint(1, Constraint.lessThanOrEqual(f2, 100000));
		solution.setConstraint(2, Constraint.lessThanOrEqual(80.0*Math.sqrt(1.0 + x3*x3) / (x3*x2), 100000));
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(3, 2, 3);
		
		solution.setVariable(0, EncodingUtils.newReal(0.00001, 100.0));
		solution.setVariable(1, EncodingUtils.newReal(0.00001, 100.0));
		solution.setVariable(2, EncodingUtils.newReal(1.0, 3.0));
		
		return solution;
	}

}
