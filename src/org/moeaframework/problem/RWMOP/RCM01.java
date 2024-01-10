package org.moeaframework.problem.RWMOP;

import org.moeaframework.core.Constraint;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.problem.AbstractProblem;

/**
 * Problem 2.1.1 - Pressure Vessel Design - from the RWMOP test problem suite.
 */
public class RCM01 extends AbstractProblem {

	public RCM01() {
		super(4, 2, 2);
	}

	@Override
	public void evaluate(Solution solution) {
		int x1 = EncodingUtils.getInt(solution.getVariable(0));
		int x2 = EncodingUtils.getInt(solution.getVariable(1));
		double x3 = EncodingUtils.getReal(solution.getVariable(2));
		double x4 = EncodingUtils.getReal(solution.getVariable(3));
		
		double z1 = 0.0625 * x1;
		double z2 = 0.0625 * x2;
		
		solution.setObjective(0, 1.7781*z1*x3*x3 + 0.6224*z1*x2*x4 + 3.1661*z1*z1*x4 + 19.84*z1*z1*x3);
		solution.setObjective(1, -Math.PI*x3*x3*x4 - (4.0 / 3.0)*Math.PI*x3*x3*x3);
		
		solution.setConstraint(0, Constraint.lessThanOrEqual(0.00954*x3, z2));
		solution.setConstraint(1, Constraint.lessThanOrEqual(0.0193*x3, z1));
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(4, 2, 2);
		
		solution.setVariable(0, EncodingUtils.newInt(1, 99));
		solution.setVariable(1, EncodingUtils.newInt(1, 99));
		solution.setVariable(2, EncodingUtils.newReal(10.0, 200.0));
		solution.setVariable(3, EncodingUtils.newReal(10.0, 200.0));
		
		return solution;
	}

}
