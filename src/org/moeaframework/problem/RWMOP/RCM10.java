package org.moeaframework.problem.RWMOP;

import org.moeaframework.core.Constraint;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.problem.AbstractProblem;

/**
 * Problem 2.1.10 - Two Bar Plane Truss - from the RWMOP test problem suite.
 */
public class RCM10 extends AbstractProblem {
	
	public RCM10() {
		super(2, 2, 2);
	}

	@Override
	public void evaluate(Solution solution) {
		double x1 = EncodingUtils.getReal(solution.getVariable(0));
		double x2 = EncodingUtils.getReal(solution.getVariable(1));
		
		final double rho = 0.283;
		final double h = 100.0;
		final double P = 104.0;
		final double E = 3e7;
		final double sig0 = 2e4;

		solution.setObjective(0, 2.0*rho*h*x2*Math.sqrt(1.0 + x1*x1));
		solution.setObjective(1, rho*h*Math.pow(1.0 + x1*x1, 1.5)*Math.pow(1.0 + x1*x1*x1*x1, 0.5)/(2.0*Math.sqrt(2.0)*E*x1*x1*x2));
		
		solution.setConstraint(0, Constraint.lessThanOrEqual(P*(1.0 + x1)*Math.sqrt(1.0 + x1*x1)/(2.0*Math.sqrt(2.0)*x1*x2), sig0));
		solution.setConstraint(1, Constraint.lessThanOrEqual(P*(1.0 - x1)*Math.sqrt(1.0 + x1*x1)/(2.0*Math.sqrt(2.0)*x1*x2), sig0));
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(2, 2, 2);
		
		solution.setVariable(0, EncodingUtils.newReal(0.1, 2.0));
		solution.setVariable(1, EncodingUtils.newReal(0.5, 2.5));
		
		return solution;
	}

}
