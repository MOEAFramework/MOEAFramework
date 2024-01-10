package org.moeaframework.problem.RWMOP;

import org.moeaframework.core.Constraint;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.problem.AbstractProblem;

/**
 * Problem 2.1.13 - Gear Design Problem - from the RWMOP test problem suite.
 */
public class RCM13 extends AbstractProblem {
	
	public RCM13() {
		super(7, 3, 11);
	}

	@Override
	public void evaluate(Solution solution) {
		double x1 = EncodingUtils.getReal(solution.getVariable(0));
		double x2 = EncodingUtils.getReal(solution.getVariable(1));
		int x3 = EncodingUtils.getInt(solution.getVariable(2));
		double x4 = EncodingUtils.getReal(solution.getVariable(3));
		double x5 = EncodingUtils.getReal(solution.getVariable(4));
		double x6 = EncodingUtils.getReal(solution.getVariable(5));
		double x7 = EncodingUtils.getReal(solution.getVariable(6));
		
		// There is a discrepancy between the paper and the reference Matlab code in f1.  The paper uses
		// 14.933/x3 but the code uses 14.833*x3.  I don't have another source to verify which is correct.

		solution.setObjective(0, 0.7854*Math.pow(x2, 2.0)*x1*(14.9334/x3 - 43.0934 + 3.3333*Math.pow(x3, 2.0)) +
				0.7854*(x5*Math.pow(x7, 2.0) + x4*Math.pow(x6, 2.0)) -
				1.508*x1*(Math.pow(x7, 2.0) + Math.pow(x6, 2.0)) +
				7.477*(Math.pow(x7, 3.0) + Math.pow(x6, 3.0)));
		solution.setObjective(1, 10.0*Math.pow(x6, -3.0) * Math.sqrt(16.91e6 + Math.pow(745.0*x4*Math.pow(x2, -1.0)*Math.pow(x3, -1.0), 2.0)));
		solution.setObjective(2, 10.0*Math.pow(x7, -3.0) * Math.sqrt(157.5e6 + Math.pow(745.0*x5*Math.pow(x2, -1.0)*Math.pow(x3, -1.0), 2.0)));
		
		solution.setConstraint(0, Constraint.lessThanOrEqual(1.0/(x1*Math.pow(x2, 2.0)*x3), 1.0/27.0));
		solution.setConstraint(1, Constraint.lessThanOrEqual(1.0/(x1*Math.pow(x2, 2.0)*Math.pow(x3, 2.0)), 1.0/397.5));
		solution.setConstraint(2, Constraint.lessThanOrEqual(1.0/(x2*Math.pow(x6, 4.0)*x3*Math.pow(x4, -3.0)), 1.0/1.93));
		solution.setConstraint(3, Constraint.lessThanOrEqual(1.0/(x2*Math.pow(x7, 4.0)*x3*Math.pow(x5, -3.0)), 1.0/1.93));
		solution.setConstraint(4, Constraint.lessThanOrEqual(10.0*Math.pow(x6, -3.0)*Math.sqrt(16.91e6 + Math.pow(745.0*x4*Math.pow(x2, -1.0)*Math.pow(x3, -1.0), 2)), 1100.0));
		solution.setConstraint(5, Constraint.lessThanOrEqual(10.0*Math.pow(x7, -3.0)*Math.sqrt(157.5e6 + Math.pow(745.0*x5*Math.pow(x2, -1.0)*Math.pow(x3, -1.0), 2)), 850.0));
		solution.setConstraint(6, Constraint.lessThanOrEqual(x2*x3, 40));
		solution.setConstraint(7, Constraint.greaterThanOrEqual(x1*Math.pow(x2, -1.0), 5.0));
		solution.setConstraint(8, Constraint.lessThanOrEqual(x1*Math.pow(x2, -1.0), 12.0));
		solution.setConstraint(9, Constraint.lessThanOrEqual(1.5*x6 - x4, -1.9));
		solution.setConstraint(10, Constraint.lessThanOrEqual(1.1*x7 - x5, -1.9));
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(7, 3, 11);
		
		solution.setVariable(0, EncodingUtils.newReal(2.6, 3.6));
		solution.setVariable(1, EncodingUtils.newReal(0.7, 0.8));
		solution.setVariable(2, EncodingUtils.newInt(17, 28));
		solution.setVariable(3, EncodingUtils.newReal(7.3, 8.3));
		solution.setVariable(4, EncodingUtils.newReal(7.3, 8.3));
		solution.setVariable(5, EncodingUtils.newReal(2.9, 3.9));
		solution.setVariable(6, EncodingUtils.newReal(5.0, 5.5));
		
		return solution;
	}

}
