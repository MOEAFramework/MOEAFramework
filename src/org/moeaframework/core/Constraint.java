package org.moeaframework.core;

/**
 * Useful methods for calculating constraints.  The returned value represents the
 * degree of constraint violation, where {@code 0.0} indicates the constraint is
 * satisfied and any non-zero value indicates a constraint violation, and can be
 * passed directly into {@link Solution#setConstraint(int, double)}.
 * <p>
 * These methods are useful as they:
 * <ol>
 *   <li>Take an {@code epsilon} value, defaulting to {@value Settings#EPS}, used to determine
 *       if two numbers are sufficiently close to be considered equal.  This helps account for
 *       rounding or computational errors introduced by floating-point numbers.
 *   <li>Calculates the magnitude of constraint violation, effectively providing a "gradient" towards
 *       the feasible solution.  This helps optimization algorithms distinguish solutions with
 *       more or less severe violations.
 * </ol>
 */
public class Constraint {
	
	private Constraint() {
		super();
	}

	/**
	 * Equality constraint ({@code x == y}).
	 * 
	 * @param x the first value
	 * @param y the second value
	 * @return the constraint value
	 */
	public static double equal(double x, double y) {
		return equal(x, y, Settings.EPS);
	}
	
	/**
	 * Equality constraint ({@code x == y}).
	 * 
	 * @param x the first value
	 * @param y the second value
	 * @param epsilon the precision when considering if two values are equal
	 * @return the constraint value
	 */
	public static double equal(double x, double y, double epsilon) {
		double diff = Math.abs(x - y);
		return diff <= epsilon ? 0.0 : diff;
	}
	
	/**
	 * Not-equals constraint ({@code x != y}).
	 * 
	 * @param x the first value
	 * @param y the second value
	 * @return the constraint value
	 */
	public static double notEqual(double x, double y) {
		return notEqual(x, y, Settings.EPS);
	}
	
	/**
	 * Not-equals constraint ({@code x != y}).
	 * 
	 * @param x the first value
	 * @param y the second value
	 * @param epsilon the precision when considering if two values are equal
	 * @return the constraint value
	 */
	public static double notEqual(double x, double y, double epsilon) {
		double diff = Math.abs(x - y);
		return diff <= epsilon ? 1.0 : 0.0;
	}
	
	/**
	 * Less than or equal constraint ({@code x <= y}).
	 * 
	 * @param x the first value
	 * @param y the second value
	 * @return the constraint value
	 */
	public static double lessThanOrEqual(double x, double y) {
		return lessThanOrEqual(x, y, Settings.EPS);
	}
	
	/**
	 * Less than or equal constraint ({@code x <= y}).
	 * 
	 * @param x the first value
	 * @param y the second value
	 * @param epsilon the precision when considering if two values are equal
	 * @return the constraint value
	 */
	public static double lessThanOrEqual(double x, double y, double epsilon) {
		double diff = Math.abs(x - y);
		return x <= y || diff <= epsilon ? 0.0 : diff;
	}
	
	/**
	 * Greater than or equal constraint ({@code x >= y}).
	 * 
	 * @param x the first value
	 * @param y the second value
	 * @return the constraint value
	 */
	public static double greaterThanOrEqual(double x, double y) {
		return greaterThanOrEqual(x, y, Settings.EPS);
	}
	
	/**
	 * Greater than or equal constraint ({@code x >= y}).
	 * 
	 * @param x the first value
	 * @param y the second value
	 * @param epsilon the precision when considering if two values are equal
	 * @return the constraint value
	 */
	public static double greaterThanOrEqual(double x, double y, double epsilon) {
		double diff = Math.abs(x - y);
		return x >= y || diff <= epsilon ? 0.0 : diff;
	}
	
	/**
	 * Less than constraint ({@code x < y}).
	 * 
	 * @param x the first value
	 * @param y the second value
	 * @return the constraint value
	 */
	public static double lessThan(double x, double y) {
		return lessThan(x, y, Settings.EPS);
	}
	
	/**
	 * Less than constraint ({@code x < y}).
	 * 
	 * @param x the first value
	 * @param y the second value
	 * @param epsilon the precision when considering if two values are equal
	 * @return the constraint value
	 */
	public static double lessThan(double x, double y, double epsilon) {
		double diff = Math.abs(x - y);
		return x < y && diff > epsilon ? 0.0 : Math.nextUp(diff);
	}
	
	/**
	 * Greater than constraint ({@code x > y}).
	 * 
	 * @param x the first value
	 * @param y the second value
	 * @return the constraint value
	 */
	public static double greaterThan(double x, double y) {
		return greaterThan(x, y, Settings.EPS);
	}
	
	/**
	 * Greater than constraint ({@code x > y}).
	 * 
	 * @param x the first value
	 * @param y the second value
	 * @param epsilon the precision when considering if two values are equal
	 * @return the constraint value
	 */
	public static double greaterThan(double x, double y, double epsilon) {
		double diff = Math.abs(x - y);
		return x > y && diff > epsilon ? 0.0 : Math.nextUp(diff);
	}

}
