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
 * The inequality constraint, or {@code c != <threshold>}.
 */
public class NotEqual extends ThresholdConstraint {

	private static final long serialVersionUID = 6500980756384192541L;

	/**
	 * Constructs a new inequality constraint.
	 * 
	 * @param threshold the threshold value
	 */
	public NotEqual(double threshold) {
		super(threshold);
	}
	
	/**
	 * Constructs a new inequality constraint.
	 * 
	 * @param threshold the threshold value
	 * @param epsilon the epsilon value
	 */
	public NotEqual(double threshold, double epsilon) {
		super(threshold, epsilon);
	}
	
	/**
	 * Constructs a new inequality constraint.
	 * 
	 * @param name the name
	 * @param threshold the threshold value
	 */
	public NotEqual(String name, double threshold) {
		super(threshold);
	}
	
	/**
	 * Constructs a new inequality constraint.
	 * 
	 * @param name the name
	 * @param threshold the threshold value
	 * @param epsilon the epsilon value
	 */
	public NotEqual(String name, double threshold, double epsilon) {
		super(threshold, epsilon);
	}
	
	/**
	 * Constructs a copy of an inequality constraint.
	 * 
	 * @param copy the constraint to copy
	 */
	public NotEqual(NotEqual copy) {
		super(copy);
	}

	@Override
	public double getMagnitudeOfViolation() {
		if (Double.isNaN(value)) {
			return 0.0;
		}
		
		double diff = Math.abs(value - threshold);
		return diff <= epsilon ? 1.0 : 0.0;
	}

	@Override
	public NotEqual copy() {
		return new NotEqual(this);
	}
	
	/**
	 * Constructs a new inequality constraint.
	 * 
	 * @param threshold the threshold value
	 * @return the constraint
	 */
	public static NotEqual to(double threshold) {
		return new NotEqual(threshold);
	}
	
	/**
	 * Constructs a new inequality constraint.
	 * 
	 * @param threshold the threshold value
	 * @param epsilon the epsilon value
	 * @return the constraint
	 */
	public static NotEqual to(double threshold, double epsilon) {
		return new NotEqual(threshold, epsilon);
	}
	
	/**
	 * Constructs a new inequality constraint.
	 * 
	 * @param name the name
	 * @param threshold the threshold value
	 * @return the constraint
	 */
	public static NotEqual to(String name, double threshold) {
		return new NotEqual(threshold);
	}
	
	/**
	 * Constructs a new inequality constraint.
	 * 
	 * @param name the name
	 * @param threshold the threshold value
	 * @param epsilon the epsilon value
	 * @return the constraint
	 */
	public static NotEqual to(String name, double threshold, double epsilon) {
		return new NotEqual(threshold, epsilon);
	}

}
