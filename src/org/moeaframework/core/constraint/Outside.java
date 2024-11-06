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
package org.moeaframework.core.constraint;

/**
 * The outside constraint, or {@code <value> < <lower> || <upper> < <value>}.
 */
public class Outside extends BoundedConstraint {

	private static final long serialVersionUID = 7023737068172462391L;

	/**
	 * Constructs a new outside constraint.
	 * 
	 * @param lower the lower threshold
	 * @param upper the upper threshold
	 */
	public Outside(double lower, double upper) {
		super(lower, upper);
	}
	
	/**
	 * Constructs a new outside constraint.
	 * 
	 * @param lower the lower threshold
	 * @param upper the upper threshold
	 * @param epsilon the epsilon value
	 */
	public Outside(double lower, double upper, double epsilon) {
		super(lower, upper, epsilon);
	}
	
	/**
	 * Constructs a new outside constraint.
	 * 
	 * @param name the name
	 * @param lower the lower threshold
	 * @param upper the upper threshold
	 */
	public Outside(String name, double lower, double upper) {
		super(name, lower, upper);
	}
	
	/**
	 * Constructs a new outside constraint.
	 * 
	 * @param name the name
	 * @param lower the lower threshold
	 * @param upper the upper threshold
	 * @param epsilon the epsilon value
	 */
	public Outside(String name, double lower, double upper, double epsilon) {
		super(name, lower, upper, epsilon);
	}
	
	/**
	 * Constructs a copy of an outside constraint.
	 * 
	 * @param copy the constraint to copy
	 */
	public Outside(Outside copy) {
		super(copy);
	}

	@Override
	public double getMagnitudeOfViolation() {
		if (Double.isNaN(value)) {
			return 0.0;
		}
		
		double diffLower = Math.abs(lower - value);
		double diffUpper = Math.abs(upper - value);
		
		if (value < lower) {	
			return diffLower > epsilon ? 0.0 : Math.nextUp(diffLower);
		}
		
		if (value > upper) {
			return diffUpper > epsilon ? 0.0 : Math.nextUp(diffUpper);
		}
		
		if (diffLower < diffUpper) {
			return diffLower;
		} else {
			return diffUpper;
		}
	}

	@Override
	public Outside copy() {
		return new Outside(this);
	}
	
	/**
	 * Constructs a new outside constraint.
	 * 
	 * @param lower the lower threshold
	 * @param upper the upper threshold
	 * @return the constraint
	 */
	public static Outside values(double lower, double upper) {
		return new Outside(lower, upper);
	}
	
	/**
	 * Constructs a new outside constraint.
	 * 
	 * @param lower the lower threshold
	 * @param upper the upper threshold
	 * @param epsilon the epsilon value
	 * @return the constraint
	 */
	public static Outside values(double lower, double upper, double epsilon) {
		return new Outside(lower, upper, epsilon);
	}
	
	/**
	 * Constructs a new outside constraint.
	 * 
	 * @param name the name
	 * @param lower the lower threshold
	 * @param upper the upper threshold
	 * @return the constraint
	 */
	public static Outside values(String name, double lower, double upper) {
		return new Outside(name, lower, upper);
	}
	
	/**
	 * Constructs a new outside constraint.
	 * 
	 * @param name the name
	 * @param lower the lower threshold
	 * @param upper the upper threshold
	 * @param epsilon the epsilon value
	 * @return the constraint
	 */
	public static Outside values(String name, double lower, double upper, double epsilon) {
		return new Outside(name, lower, upper, epsilon);
	}

}
