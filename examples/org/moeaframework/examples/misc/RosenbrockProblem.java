package org.moeaframework.examples.misc;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.problem.AbstractProblem;

public class RosenbrockProblem extends AbstractProblem {
	
	public RosenbrockProblem() {
		super(2, 1, 0);
	}

	@Override
	public void evaluate(Solution solution) {
		double result = 0.0;
		double[] x = EncodingUtils.getReal(solution);

		for (int i = 0; i < x.length-1; i++) {
			result += 100 * (x[i]*x[i] - x[i+1])*(x[i]*x[i] - x[i+1]) + (x[i] - 1)*(x[i] - 1);
		}

		solution.setObjective(0, result);
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(2, 1, 0);
		solution.setVariable(0, EncodingUtils.newReal(-10, 10));
		solution.setVariable(1, EncodingUtils.newReal(-10, 10));
		return solution;
	}

}