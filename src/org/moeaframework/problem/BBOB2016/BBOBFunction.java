package org.moeaframework.problem.BBOB2016;

import org.moeaframework.core.Solution;
import org.moeaframework.problem.AbstractProblem;

/**
 * Abstract class for the functions provided by the BBOB test suite.  These
 * functions are exclusively single-objective.
 */
public abstract class BBOBFunction extends AbstractProblem {

	/**
	 * Constructs a new function for the BBOB test suite.
	 * 
	 * @param numberOfVariables the number of decision variables
	 */
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
