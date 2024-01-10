package org.moeaframework.problem.RWMOP;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.problem.AbstractProblem;

/**
 * Problem 2.1.9 - Four Bar Plane Truss - from the RWMOP test problem suite.
 */
public class RCM09 extends AbstractProblem {
	
	private final double F = 10.0;
	private final double E = 2e5;
	private final double L = 200.0;
	private final double sig = 10.0;

	public RCM09() {
		super(4, 2, 0);
	}

	@Override
	public void evaluate(Solution solution) {
		double x1 = EncodingUtils.getReal(solution.getVariable(0));
		double x2 = EncodingUtils.getReal(solution.getVariable(1));
		double x3 = EncodingUtils.getReal(solution.getVariable(2));
		double x4 = EncodingUtils.getReal(solution.getVariable(3));

		solution.setObjective(0, L*(2.0*x1 + Math.sqrt(2.0)*x2 + Math.sqrt(2.0)*x3 + x4));
		solution.setObjective(1, F * L/E * (2.0/x1 + 2.0*Math.sqrt(2.0)/x2 - 2.0*Math.sqrt(2.0)/x3 + 2.0/x4));
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(4, 2, 0);
		
		double f = F / sig;
		
		solution.setVariable(0, EncodingUtils.newReal(f, 3.0*f));
		solution.setVariable(1, EncodingUtils.newReal(Math.sqrt(2.0)*f, 3.0*f));
		solution.setVariable(2, EncodingUtils.newReal(Math.sqrt(2.0)*f, 3.0*f));
		solution.setVariable(3, EncodingUtils.newReal(f, 3.0*f));
		
		return solution;
	}

}
