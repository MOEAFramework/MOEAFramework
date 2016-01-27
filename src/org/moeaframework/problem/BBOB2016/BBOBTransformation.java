package org.moeaframework.problem.BBOB2016;

import org.moeaframework.core.Solution;

/**
 * Abstract class for transformations provided by the BBOB test suite.
 */
public abstract class BBOBTransformation extends BBOBFunction {

	/**
	 * The inner function that is being transformed.
	 */
	protected BBOBFunction function;
	
	/**
	 * Constructs a new instance of a BBOB test suite transformation.
	 * 
	 * @param function the inner function that is being transformed
	 */
	public BBOBTransformation(BBOBFunction function) {
		super(function.getNumberOfVariables());
		this.function = function;
	}
	
	@Override
	public Solution newSolution() {
		return function.newSolution();
	}

}
