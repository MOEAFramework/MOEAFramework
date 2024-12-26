/* Copyright 2009-2025 David Hadka
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
 * The less than constraint, or {@code <value> < <threshold>}.
 */
public class LessThan extends ThresholdConstraint {

	private static final long serialVersionUID = 3513346524087161353L;

	/**
	 * Constructs a new less than constraint.
	 * 
	 * @param threshold the threshold value
	 */
	public LessThan(double threshold) {
		super(threshold);
	}
	
	/**
	 * Constructs a new less than constraint.
	 * 
	 * @param threshold the threshold value
	 * @param epsilon the epsilon value
	 */
	public LessThan(double threshold, double epsilon) {
		super(threshold, epsilon);
	}
	
	/**
	 * Constructs a new less than constraint.
	 * 
	 * @param name the name
	 * @param threshold the threshold value
	 */
	public LessThan(String name, double threshold) {
		super(name, threshold);
	}
	
	/**
	 * Constructs a new less than constraint.
	 * 
	 * @param name the name
	 * @param threshold the threshold value
	 * @param epsilon the epsilon value
	 */
	public LessThan(String name, double threshold, double epsilon) {
		super(name, threshold, epsilon);
	}
	
	/**
	 * Constructs a copy of a less than constraint.
	 * 
	 * @param copy the constraint to copy
	 */
	public LessThan(LessThan copy) {
		super(copy);
	}

	@Override
	public double getMagnitudeOfViolation() {
		if (Double.isNaN(value)) {
			return 0.0;
		}
		
		double diff = Math.abs(value - threshold);
		return value < threshold && diff > epsilon ? 0.0 : Math.nextUp(diff);
	}

	@Override
	public LessThan copy() {
		return new LessThan(this);
	}
	
	/**
	 * Constructs a new less than constraint.
	 * 
	 * @param threshold the threshold value
	 * @return the constraint
	 */
	public static LessThan value(double threshold) {
		return new LessThan(threshold);
	}
	
	/**
	 * Constructs a new less than constraint.
	 * 
	 * @param threshold the threshold value
	 * @param epsilon the epsilon value
	 * @return the constraint
	 */
	public static LessThan value(double threshold, double epsilon) {
		return new LessThan(threshold, epsilon);
	}
	
	/**
	 * Constructs a new less than constraint.
	 * 
	 * @param name the name
	 * @param threshold the threshold value
	 * @return the constraint
	 */
	public static LessThan value(String name, double threshold) {
		return new LessThan(name, threshold);
	}
	
	/**
	 * Constructs a new less than constraint.
	 * 
	 * @param name the name
	 * @param threshold the threshold value
	 * @param epsilon the epsilon value
	 * @return the constraint
	 */
	public static LessThan value(String name, double threshold, double epsilon) {
		return new LessThan(name, threshold, epsilon);
	}

}
