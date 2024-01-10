package org.moeaframework.problem.RWMOP;

import org.moeaframework.core.Constraint;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.problem.AbstractProblem;

/**
 * Problem 2.1.4 - Welded Beam Design - from the RWMOP test problem suite.
 */
public class RCM04 extends AbstractProblem {

	public RCM04() {
		super(4, 2, 4);
	}

	@Override
	public void evaluate(Solution solution) {
		double x1 = EncodingUtils.getReal(solution.getVariable(0));
		double x2 = EncodingUtils.getReal(solution.getVariable(1));
		double x3 = EncodingUtils.getReal(solution.getVariable(2));
		double x4 = EncodingUtils.getReal(solution.getVariable(3));
		
		final double P = 6000.0;
		final double L = 14.0;
		final double E = 30e6;
		final double tmax = 13600.0;
		final double sigmax = 30000.0;
		final double G = 12e6;
		
		double Pc = 4.013*E*Math.sqrt((Math.pow(x3, 2.0) + Math.pow(x4, 6.0)) / 36.0) / Math.pow(L, 2.0) * (1.0 - x3/(2.0*L) * Math.sqrt(E/(4*G)));
		double sigma = (6.0*P*L) / (x4*Math.pow(x3, 2.0));
		double J = 2.0*Math.sqrt(2.0)*x1*x2*(Math.pow(x2, 2.0)/12.0 + Math.pow((x1 + x3) / 2.0, 2.0));
		double R = Math.sqrt(Math.pow(x2, 2.0)/4.0 + Math.pow((x1 + x3) / 2.0, 2.0));
		double M = P*(L + x2/2.0);
		double tho1 = P / (Math.sqrt(2.0)*x1*x2);
		double tho2 = M*R/J;
		double tho = Math.sqrt(Math.pow(tho1, 2.0) + 2*tho1*tho2*x2/(2.0*R) + Math.pow(tho2, 2.0));

		solution.setObjective(0, 1.10471*Math.pow(x1, 2.0)*x2 + 0.04811*x3*x4*(14.0 + x2));
		solution.setObjective(1, (4.0*P*Math.pow(L, 3.0)) / (E*x4*Math.pow(x3, 3.0)));
		
		solution.setConstraint(0, Constraint.lessThanOrEqual(tho, tmax));
		solution.setConstraint(1, Constraint.lessThanOrEqual(sigma, sigmax));
		solution.setConstraint(2, Constraint.lessThanOrEqual(x1, x4));
		solution.setConstraint(3, Constraint.lessThanOrEqual(P, Pc));
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(4, 2, 4);
		
		solution.setVariable(0, EncodingUtils.newReal(0.125, 5.0));
		solution.setVariable(1, EncodingUtils.newReal(0.1, 10.0));
		solution.setVariable(2, EncodingUtils.newReal(0.1, 10.0));
		solution.setVariable(3, EncodingUtils.newReal(0.125, 5.0));
		
		return solution;
	}

}
