package org.moeaframework.problem.RWMOP;

import org.moeaframework.core.Constraint;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.problem.AbstractProblem;

/**
 * Problem 2.1.6 - Speed Reducer Design - from the RWMOP test problem suite.
 */
public class RCM06 extends AbstractProblem {

	public RCM06() {
		super(7, 2, 11);
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

		solution.setObjective(0,
				0.7854*x1*Math.pow(x2, 2.0)*(10.0*Math.pow(x3, 2.0)/3.0 + 14.933*x3 - 43.0934) - 
				1.508*x1*(Math.pow(x6, 2.0) + Math.pow(x7, 2.0)) +
				7.477*(Math.pow(x6, 3.0) + Math.pow(x7, 3.0)) +
				0.7854*(x4*Math.pow(x6, 2.0) + x5*Math.pow(x7, 2.0)));
		solution.setObjective(1, Math.sqrt(Math.pow(745.0*x4/(x2*x3), 2.0) + 1.69e7) / (0.1*Math.pow(x6, 3.0)));
		
		// TODO: The paper uses the constant 110 for constraint 11 but the Matlab code uses 850.  For now using the
		// Matlab code, but will need to track down the correct value.
		
		solution.setConstraint(0, Constraint.lessThanOrEqual(1.0/(x1*Math.pow(x2, 2.0)*x3), 1.0/27.0));
		solution.setConstraint(1, Constraint.lessThanOrEqual(1.0/(x1*Math.pow(x2, 2.0)*Math.pow(x3, 2.0)), 1.0/397.5));
		solution.setConstraint(2, Constraint.lessThanOrEqual(Math.pow(x4, 3.0)/(x2*x3*Math.pow(x6, 4.0)), 1.0/1.93));
		solution.setConstraint(3, Constraint.lessThanOrEqual(Math.pow(x5, 3.0)/(x2*x3*Math.pow(x7, 4.0)), 1.0/1.93));
		solution.setConstraint(4, Constraint.lessThanOrEqual(x2*x3, 40.0));
		solution.setConstraint(5, Constraint.lessThanOrEqual(x1/x2, 12.0));
		solution.setConstraint(6, Constraint.greaterThanOrEqual(x1/x2, 5.0));
		solution.setConstraint(7, Constraint.lessThanOrEqual(1.9, x4 - 1.5*x6));
		solution.setConstraint(8, Constraint.lessThanOrEqual(1.9, x5 - 1.1*x7));
		solution.setConstraint(9, Constraint.lessThanOrEqual(solution.getObjective(1), 1300.0));
		solution.setConstraint(10, Constraint.lessThanOrEqual(Math.sqrt(Math.pow(745.0*x5/(x2*x3), 2.0) + 1.575e8) / (0.1*Math.pow(x7, 3.0)), 850.0));
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(7, 2, 11);
		
		solution.setVariable(0, EncodingUtils.newReal(2.6, 3.6));
		solution.setVariable(1, EncodingUtils.newReal(0.7, 0.8));
		solution.setVariable(2, EncodingUtils.newInt(17, 28));
		solution.setVariable(3, EncodingUtils.newReal(7.3, 8.3));
		solution.setVariable(4, EncodingUtils.newReal(7.3, 8.3));
		solution.setVariable(5, EncodingUtils.newReal(2.9, 3.9));
		solution.setVariable(6, EncodingUtils.newReal(5, 5.5));
		
		return solution;
	}

}
