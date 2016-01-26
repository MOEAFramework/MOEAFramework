package org.moeaframework.problem.BBOB2016;

import org.moeaframework.core.Solution;
import org.moeaframework.problem.AbstractProblem;

/**
 * Abstract class for the functions and transformations provided by the BBOB
 * test suite.
 */
public abstract class BBOBFunction extends AbstractProblem {

	public BBOBFunction(int numberOfVariables) {
		super(numberOfVariables, 1);
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(numberOfVariables, 1);
		
		for (int i = 0; i < numberOfVariables; i++) {
			solution.setVariable(i, BBOBUtils.createTransformedVariable());
		}
		
		return solution;
	}

}
