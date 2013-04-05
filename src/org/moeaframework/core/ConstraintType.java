package org.moeaframework.core;

/**
 * Differentiates equality and inequality constraints.
 */
public enum ConstraintType {
	
	/**
	 * An inequality constraint which is feasible if and only if the constraint
	 * is less than or equal to zero (i.e., {@code constraint <= 0}).
	 */
	LESS_THAN_OR_EQUAL,
	
	/**
	 * An inequality constraint which is feasible if and only if the constraint
	 * is greater than or equal to zero (i.e., {@code constraint >= 0}).
	 */
	GREATER_THAN_OR_EQUAL,
	
	/**
	 * An equality constraint which is feasible if and only if the constraint
	 * is equal to zero (i.e., {@code constraint == 0}). 
	 */
	EQUAL

}
