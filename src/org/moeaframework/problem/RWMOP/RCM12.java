package org.moeaframework.problem.RWMOP;

import org.moeaframework.core.Constraint;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.problem.AbstractProblem;

/**
 * Problem 2.1.12 - Simply Supported I-beam Design - from the RWMOP test problem suite.
 */
public class RCM12 extends AbstractProblem {
	
	public RCM12() {
		super(4, 2, 1);
	}

	@Override
	public void evaluate(Solution solution) {
		double x1 = EncodingUtils.getReal(solution.getVariable(0));
		double x2 = EncodingUtils.getReal(solution.getVariable(1));
		double x3 = EncodingUtils.getReal(solution.getVariable(2));
		double x4 = EncodingUtils.getReal(solution.getVariable(3));
		
		final double P = 600.0;
		final double L = 200.0;
		final double E = 20000.0;
		
		double d1 = x3*Math.pow(x1 - 2.0*x4, 3.0) + 2.0*x2*x4*(4.0*Math.pow(x4, 2.0) + 3.0*x1*(x1 - 2.0*x4));
		double d2 = (x1 - 2.0*x4)*Math.pow(x3, 3.0) + 2.0*x4*Math.pow(x2, 3.0);
		
		solution.setObjective(0, 2.0*x2*x4 + x3*(x1 - 2*x4));
		solution.setObjective(1, P*Math.pow(L, 3.0) / (4.0*E*d1));
		
		solution.setConstraint(0, Constraint.lessThanOrEqual(180000.0*x1/d1 + 15000.0*x2/d2, 16.0));
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(4, 2, 1);
		
		solution.setVariable(0, EncodingUtils.newReal(10.0, 80.0));
		solution.setVariable(1, EncodingUtils.newReal(10.0, 50.0));
		solution.setVariable(2, EncodingUtils.newReal(0.9, 5.0));
		solution.setVariable(3, EncodingUtils.newReal(0.9, 5.0));
		
		return solution;
	}

}
