package org.moeaframework.problem.RWMOP;

import org.moeaframework.core.Constraint;
import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.problem.AbstractProblem;

/**
 * Problem 2.1.11 - Water Resource Management - from the RWMOP test problem suite.
 */
public class RCM11 extends AbstractProblem {
	
	public RCM11() {
		super(3, 5, 7);
	}

	@Override
	public void evaluate(Solution solution) {
		double x1 = EncodingUtils.getReal(solution.getVariable(0));
		double x2 = EncodingUtils.getReal(solution.getVariable(1));
		double x3 = EncodingUtils.getReal(solution.getVariable(2));

		solution.setObjective(0, 106780.37*(x2 + x3) + 61704.67);
		solution.setObjective(1, 3000.0*x1);
		solution.setObjective(2, 305700.0*2289.0*x2/Math.pow(0.06*2289.0, 0.65));
		solution.setObjective(3, 250.0*2289.0*Math.exp(-39.75*x2 + 9.9*x3 + 2.74));
		solution.setObjective(4, 25.0*(1.39/(x1*x2) + 4940.0*x3 - 80));
		
		solution.setConstraint(0, Constraint.lessThanOrEqual(0.00139/(x1*x2) + 4.94*x3 - 0.08, 1.0));
		solution.setConstraint(1, Constraint.lessThanOrEqual(0.000306/(x1*x2) + 1.082*x3 - 0.0986, 1.0));
		solution.setConstraint(2, Constraint.lessThanOrEqual(12.307/(x1*x2) + 49408.24*x3 + 4051.02, 50000.0));
		solution.setConstraint(3, Constraint.lessThanOrEqual(2.098/(x1*x2) + 8046.33*x3 - 696.71, 16000.0));
		solution.setConstraint(4, Constraint.lessThanOrEqual(2.138/(x1*x2) + 7883.39*x3 - 705.04, 10000.0));
		solution.setConstraint(5, Constraint.lessThanOrEqual(0.417*x1*x2 + 1721.26*x3 - 136.54, 2000.0));
		solution.setConstraint(6, Constraint.lessThanOrEqual(0.164/(x1*x2) + 631.13*x3 - 54.48, 550.0));
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(3, 5, 7);
		
		solution.setVariable(0, EncodingUtils.newReal(0.01, 0.45));
		solution.setVariable(1, EncodingUtils.newReal(0.01, 0.1));
		solution.setVariable(2, EncodingUtils.newReal(0.01, 0.1));
		
		return solution;
	}

}
