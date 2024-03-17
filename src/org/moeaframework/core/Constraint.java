/* Copyright 2009-2024 David Hadka
 *
 * This file is part of the MOEA Framework.
 *
 * The MOEA Framework is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * The MOEA Framework is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the MOEA Framework.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.moeaframework.core;

/**
 * Useful methods for calculating constraints.  The returned value represents the degree of constraint violation,
 * where {@code 0.0} indicates the constraint is satisfied and any non-zero value indicates a constraint violation,
 * and can be passed directly into {@link Solution#setConstraint(int, double)}.
 * <p>
 * These methods are useful as they:
 * <ol>
 *   <li>Take an {@code epsilon} value, defaulting to {@value Settings#EPS}, used to determine if two numbers are
 *       sufficiently close to be considered equal.  This helps account for rounding or computational errors
 *       introduced by floating-point numbers.
 *   <li>Calculates the magnitude of constraint violation, effectively providing a "gradient" towards the feasible
 *       solution.  This helps optimization algorithms distinguish solutions with more or less severe violations.
 * </ol>
 */
public class Constraint {

	/**
	 * Constant used to indicate a constraint is satisfied.
	 */
	public static final double SATISFIED = 0.0;
	
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
		return x >= y || diff <= epsilon ? 0.0 : -diff;
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
		return x > y && diff > epsilon ? 0.0 : Math.nextDown(-diff);
	}
	
	/**
	 * Constraint requiring the value to be between a lower and upper bounds ({@code l <= x <= u}).
	 * 
	 * @param lower the lower bound
	 * @param value the value
	 * @param upper the upper bound
	 * @return the constraint value
	 */
	public static double between(double lower, double value, double upper) {
		return between(lower, value, upper, Settings.EPS);
	}
	
	/**
	 * Constraint requiring the value to be between a lower and upper bounds ({@code l <= x <= u}).
	 * 
	 * @param lower the lower bound
	 * @param value the value
	 * @param upper the upper bound
	 * @param epsilon the precision when considering if two values are equal
	 * @return the constraint value
	 */
	public static double between(double lower, double value, double upper, double epsilon) {
		if (value < lower) {
			double diff = Math.abs(lower - value);
			return diff <= epsilon ? 0.0 : -diff;
		}
		
		if (value > upper) {
			double diff = Math.abs(upper - value);
			return diff <= epsilon ? 0.0 : diff;
		}
		
		return 0.0;
	}
	
	/**
	 * Constraint requiring the value to be outside a given range ({@code l < x && x > u}).
	 * 
	 * @param lower the lower bound
	 * @param value the value
	 * @param upper the upper bound
	 * @return the constraint value
	 */
	public static double outside(double lower, double value, double upper) {
		return outside(lower, value, upper, Settings.EPS);
	}
	
	/**
	 * Constraint requiring the value to be outside a given range ({@code x < l && x > u}).
	 * 
	 * @param lower the lower bound
	 * @param value the value
	 * @param upper the upper bound
	 * @param epsilon the precision when considering if two values are equal
	 * @return the constraint value
	 */
	public static double outside(double lower, double value, double upper, double epsilon) {
		double diffLower = Math.abs(lower - value);
		double diffUpper = Math.abs(upper - value);
		
		if (value < lower) {	
			return diffLower > epsilon ? 0.0 : Math.nextUp(diffLower);
		}
		
		if (value > upper) {
			return diffUpper > epsilon ? 0.0 : Math.nextDown(-diffUpper);
		}
		
		if (diffLower < diffUpper) {
			return diffLower;
		} else {
			return -diffUpper;
		}
	}

}
