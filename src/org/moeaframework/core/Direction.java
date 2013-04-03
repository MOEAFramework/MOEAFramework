package org.moeaframework.core;

/**
 * Specifies the direction in which an objective is optimized.  A minimized
 * objective is optimized towards negative infinity; a maximized objective
 * is optimized towards positive infinity.
 */
public enum Direction {
	
	/**
	 * Indicates the objective is minimized towards negative infinity.
	 */
	MINIMIZE,
	
	/**
	 * Indicates the objective is maximized towards positive infinity.
	 */
	MAXIMIZE

}
