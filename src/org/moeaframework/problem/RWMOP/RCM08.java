package org.moeaframework.problem.RWMOP;

import org.moeaframework.core.Constraint;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.problem.AbstractProblem;

/**
 * Problem 2.1.8 - Car Side Impact - from the RWMOP test problem suite.
 */
public class RCM08 extends AbstractProblem {

	public RCM08() {
		super(7, 3, 10);
	}

	@Override
	public void evaluate(Solution solution) {
		double x1 = EncodingUtils.getReal(solution.getVariable(0));
		double x2 = EncodingUtils.getReal(solution.getVariable(1));
		double x3 = EncodingUtils.getReal(solution.getVariable(2));
		double x4 = EncodingUtils.getReal(solution.getVariable(3));
		double x5 = EncodingUtils.getReal(solution.getVariable(4));
		double x6 = EncodingUtils.getReal(solution.getVariable(5));
		double x7 = EncodingUtils.getReal(solution.getVariable(6));

		double F = 4.72 - 0.5*x4 - 0.19*x2*x3;
		double Vmbp = 10.58 - 0.674*x1*x2 - 0.67275*x2;
		double Vfd = 16.45 - 0.489*x3*x7 - 0.843*x5*x6;
		double f1 = 1.98 + 4.9*x1 + 6.67*x2 + 6.98*x3 + 4.01*x4 + 1.78*x5 + 0.00001*x6 + 2.73*x7;
		double f2 = F;
		double f3 = 0.5*(Vmbp + Vfd);
		double g1 = 1.16 - 0.3717*x2*x4 - 0.0092928*x3;
		double g2 = 0.261 - 0.0159*x1*x2 - 0.06486*x1 - 0.019*x2*x7 + 0.0144*x3*x5 + 0.0154464*x6;
		double g3 = 0.214 + 0.00817*x5 - 0.045195*x1 - 0.0135168*x1 + 0.03099*x2*x6 - 0.018*x2*x7 + 0.007176*x3 + 0.023232*x3 - 0.00364*x5*x6 - 0.018*x2*x2;
		double g4 = 0.74 - 0.61*x2 - 0.031296*x3 - 0.031872*x7 + 0.227*x2*x2;
		double g5 = 28.98 + 3.818*x3 - 4.2*x1*x2 + 1.27296*x6 - 2.68065*x7;
		double g6 = 33.86 + 2.95*x3 - 5.057*x1*x2 - 3.795*x2 - 3.4431*x7 + 1.45728;
		double g7 = 46.36 - 9.9*x2 - 4.4505*x1;
		double g8 = F;
		double g9 = Vmbp;
		double g10 = Vfd;
		
		solution.setObjective(0, f1);
		solution.setObjective(1, f2);
		solution.setObjective(2, f3);
		
		solution.setConstraint(0, Constraint.lessThanOrEqual(g1, 1.0));
		solution.setConstraint(1, Constraint.lessThanOrEqual(g2, 0.32));
		solution.setConstraint(2, Constraint.lessThanOrEqual(g3, 0.32));
		solution.setConstraint(3, Constraint.lessThanOrEqual(g4, 0.32));
		solution.setConstraint(4, Constraint.lessThanOrEqual(g5, 32.0));
		solution.setConstraint(5, Constraint.lessThanOrEqual(g6, 32.0));
		solution.setConstraint(6, Constraint.lessThanOrEqual(g7, 32.0));
		solution.setConstraint(7, Constraint.lessThanOrEqual(g8, 4.0));
		solution.setConstraint(8, Constraint.lessThanOrEqual(g9, 9.9));
		solution.setConstraint(9, Constraint.lessThanOrEqual(g10, 15.7));
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(7, 3, 10);
		
		solution.setVariable(0, EncodingUtils.newReal(0.5, 1.5));
		solution.setVariable(1, EncodingUtils.newReal(0.45, 1.35));
		solution.setVariable(2, EncodingUtils.newReal(0.5, 1.5));
		solution.setVariable(3, EncodingUtils.newReal(0.5, 1.5));
		solution.setVariable(4, EncodingUtils.newReal(0.875, 2.625));
		solution.setVariable(5, EncodingUtils.newReal(0.4, 1.2));
		solution.setVariable(6, EncodingUtils.newReal(0.4, 1.2));
		
		return solution;
	}

}
