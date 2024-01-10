package org.moeaframework.problem.RWMOP;

import org.moeaframework.core.Constraint;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.problem.AbstractProblem;

/**
 * Problem 2.1.2 - Vibrating Platform - from the RWMOP test problem suite.
 */
public class RCM02 extends AbstractProblem {

	public RCM02() {
		super(5, 2, 5);
	}

	@Override
	public void evaluate(Solution solution) {
		double d1 = EncodingUtils.getReal(solution.getVariable(0));
		double d2 = EncodingUtils.getReal(solution.getVariable(1));
		double d3 = EncodingUtils.getReal(solution.getVariable(2));
		double b = EncodingUtils.getReal(solution.getVariable(3));
		double L = EncodingUtils.getReal(solution.getVariable(4));
		
		final double rho1 = 100.0;
		final double rho2 = 2770.0;
		final double rho3 = 7780.0;
		final double E1 = 1.6;
		final double E2 = 70.0;
		final double E3 = 200.0;
		final double c1 = 500.0;
		final double c2 = 1500.0;
		final double c3 = 800.0;
		
		// The reference paper and Matlab implementation differ in two locations:
		//
		//    1. The code computes EI using rho3 instead of E3
		//    2. The code computes f1 using -pi/(2L)^2 whereas the paper shows -pi/2L^2
		//
		// In both cases we use the equations shown in the paper and confirmed by other sources.
		
		double mu = 2.0*b*(rho1*d1 + rho2*(d2 - d1) + rho3*(d3 - d2));
		double EI = (2.0*b/3.0) * (E1*d1*d1*d1 + E2*(d2*d2*d2 - d1*d1*d1) + E3*(d3 - d2)); 
		
		solution.setObjective(0, -Math.PI/(2.0*L*L) * Math.sqrt(Math.abs(EI/mu)));
		solution.setObjective(1, 2*b*L*(c1*d1 + c2*(d2 - d1) + c3*(d3 - d2)));
		
		solution.setConstraint(0, Constraint.lessThanOrEqual(mu*L, 2800.0));
		solution.setConstraint(1, Constraint.lessThanOrEqual(d1, d2));
		solution.setConstraint(2, Constraint.lessThanOrEqual(d2 - d1, 0.15));
		solution.setConstraint(3, Constraint.lessThanOrEqual(d2, d3));
		solution.setConstraint(4, Constraint.lessThanOrEqual(d3 - d2, 0.01));
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(5, 2, 5);
		
		solution.setVariable(0, EncodingUtils.newReal(0.05, 0.5));
		solution.setVariable(1, EncodingUtils.newReal(0.2, 0.5));
		solution.setVariable(2, EncodingUtils.newReal(0.2, 0.6));
		solution.setVariable(3, EncodingUtils.newReal(0.35, 0.5));
		solution.setVariable(4, EncodingUtils.newReal(3, 6));
		
		return solution;
	}

}
