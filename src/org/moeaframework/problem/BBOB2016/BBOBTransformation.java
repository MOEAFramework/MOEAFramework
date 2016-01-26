package org.moeaframework.problem.BBOB2016;

import org.moeaframework.core.Solution;

public abstract class BBOBTransformation extends BBOBFunction {

	protected BBOBFunction function;
	
	public BBOBTransformation(BBOBFunction function) {
		super(function.getNumberOfVariables());
	}
	
	@Override
	public Solution newSolution() {
		return function.newSolution();
	}

}
