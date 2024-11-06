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
 * The equality constraint, or {@code <value> == <threshold>}.
 */
public class Equal extends ThresholdConstraint {

	private static final long serialVersionUID = -2766984574651872793L;

	/**
	 * Constructs a new equality constraint.
	 * 
	 * @param threshold the threshold value
	 */
	public Equal(double threshold) {
		super(threshold);
	}
	
	/**
	 * Constructs a new equality constraint.
	 * 
	 * @param threshold the threshold value
	 * @param epsilon the epsilon value
	 */
	public Equal(double threshold, double epsilon) {
		super(threshold, epsilon);
	}
	
	/**
	 * Constructs a new equality constraint.
	 * 
	 * @param name the name
	 * @param threshold the threshold value
	 */
	public Equal(String name, double threshold) {
		super(name, threshold);
	}
	
	/**
	 * Constructs a new equality constraint.
	 * 
	 * @param name the name
	 * @param threshold the threshold value
	 * @param epsilon the epsilon value
	 */
	public Equal(String name, double threshold, double epsilon) {
		super(name, threshold, epsilon);
	}
	
	/**
	 * Constructs a copy of an equal constraint.
	 * 
	 * @param copy the constraint to copy
	 */
	public Equal(Equal copy) {
		super(copy);
	}

	@Override
	public double getMagnitudeOfViolation() {
		if (Double.isNaN(value)) {
			return 0.0;
		}
		
		double diff = Math.abs(value - threshold);
		return diff <= epsilon ? 0.0 : diff;
	}

	@Override
	public Equal copy() {
		return new Equal(this);
	}
	
	/**
	 * Constructs a new equality constraint.
	 * 
	 * @param threshold the threshold value
	 * @return the constraint
	 */
	public static Equal to(double threshold) {
		return new Equal(threshold);
	}
	
	/**
	 * Constructs a new equality constraint.
	 * 
	 * @param threshold the threshold value
	 * @param epsilon the epsilon value
	 * @return the constraint
	 */
	public static Equal to(double threshold, double epsilon) {
		return new Equal(threshold, epsilon);
	}
	
	/**
	 * Constructs a new equality constraint.
	 * 
	 * @param name the name
	 * @param threshold the threshold value
	 * @return the constraint
	 */
	public static Equal to(String name, double threshold) {
		return new Equal(name, threshold);
	}
	
	/**
	 * Constructs a new equality constraint.
	 * 
	 * @param name the name
	 * @param threshold the threshold value
	 * @param epsilon the epsilon value
	 * @return the constraint
	 */
	public static Equal to(String name, double threshold, double epsilon) {
		return new Equal(name, threshold, epsilon);
	}

}
