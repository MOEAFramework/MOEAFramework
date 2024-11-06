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
 * The greater than constraint, or {@code c > <threshold>}.
 */
public class GreaterThan extends ThresholdConstraint {

	private static final long serialVersionUID = -7048327561238629686L;

	/**
	 * Constructs a new greater than constraint.
	 * 
	 * @param threshold the threshold value
	 */
	public GreaterThan(double threshold) {
		super(threshold);
	}
	
	/**
	 * Constructs a new greater than constraint.
	 * 
	 * @param threshold the threshold value
	 * @param epsilon the epsilon value
	 */
	public GreaterThan(double threshold, double epsilon) {
		super(threshold, epsilon);
	}
	
	/**
	 * Constructs a new greater than constraint.
	 * 
	 * @param name the name
	 * @param threshold the threshold value
	 */
	public GreaterThan(String name, double threshold) {
		super(name, threshold);
	}
	
	/**
	 * Constructs a new greater than constraint.
	 * 
	 * @param name the name
	 * @param threshold the threshold value
	 * @param epsilon the epsilon value
	 */
	public GreaterThan(String name, double threshold, double epsilon) {
		super(name, threshold, epsilon);
	}
	
	/**
	 * Constructs a copy of a greater than constraint.
	 * 
	 * @param copy the constraint to copy
	 */
	public GreaterThan(GreaterThan copy) {
		super(copy);
	}

	@Override
	public double getMagnitudeOfViolation() {
		if (Double.isNaN(value)) {
			return 0.0;
		}
		
		double diff = Math.abs(value - threshold);
		return value > threshold && diff > epsilon ? 0.0 : Math.nextUp(diff);
	}

	@Override
	public GreaterThan copy() {
		return new GreaterThan(this);
	}
	
	/**
	 * Constructs a new greater than constraint.
	 * 
	 * @param threshold the threshold value
	 * @return the constraint
	 */
	public static GreaterThan value(double threshold) {
		return new GreaterThan(threshold);
	}
	
	/**
	 * Constructs a new greater than constraint.
	 * 
	 * @param threshold the threshold value
	 * @param epsilon the epsilon value
	 * @return the constraint
	 */
	public static GreaterThan value(double threshold, double epsilon) {
		return new GreaterThan(threshold, epsilon);
	}
	
	/**
	 * Constructs a new greater than constraint.
	 * 
	 * @param name the name
	 * @param threshold the threshold value
	 * @return the constraint
	 */
	public static GreaterThan value(String name, double threshold) {
		return new GreaterThan(name, threshold);
	}
	
	/**
	 * Constructs a new greater than constraint.
	 * 
	 * @param name the name
	 * @param threshold the threshold value
	 * @param epsilon the epsilon value
	 * @return the constraint
	 */
	public static GreaterThan value(String name, double threshold, double epsilon) {
		return new GreaterThan(name, threshold, epsilon);
	}

}
