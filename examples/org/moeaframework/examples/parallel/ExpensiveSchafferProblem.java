package org.moeaframework.examples.parallel;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.EncodingUtils;
import org.moeaframework.problem.AbstractProblem;

public class ExpensiveSchafferProblem extends AbstractProblem {

	public ExpensiveSchafferProblem() {
		super(1, 2);
	}

	@Override
	public void evaluate(Solution solution) {
		double x = EncodingUtils.getReal(solution.getVariable(0));
		
		// perform some expensive calculation
		double sum = 0.0;
		
		for (int i = 0; i < 1000000; i++) {
			sum += i;
		}
		
		solution.setObjective(0, Math.pow(x, 2.0));
		solution.setObjective(1, Math.pow(x - 2.0, 2.0));
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(1, 2);
		solution.setVariable(0, EncodingUtils.newReal(-10.0, 10.0));
		return solution;
	}

}
