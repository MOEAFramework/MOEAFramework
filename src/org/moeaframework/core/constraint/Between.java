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
 * The between constraint, or {@code <lower> <= c <= <upper>}.
 */
public class Between extends BoundedConstraint {

	private static final long serialVersionUID = 192194143362935822L;

	public Between(double lower, double upper) {
		super(lower, upper);
	}
	
	public Between(double lower, double upper, double epsilon) {
		super(lower, upper, epsilon);
	}
	
	public Between(String name, double lower, double upper) {
		super(name, lower, upper);
	}
	
	public Between(String name, double lower, double upper, double epsilon) {
		super(name, lower, upper, epsilon);
	}
	
	public Between(Between copy) {
		super(copy);
	}

	@Override
	public double getMagnitudeOfViolation() {
		if (Double.isNaN(value)) {
			return 0.0;
		}
		
		if (value < lower) {
			double diff = Math.abs(lower - value);
			return diff <= epsilon ? 0.0 : diff;
		}
		
		if (value > upper) {
			double diff = Math.abs(upper - value);
			return diff <= epsilon ? 0.0 : diff;
		}
		
		return 0.0;
	}

	@Override
	public Between copy() {
		return new Between(this);
	}
	
	public static Between values(double lower, double upper) {
		return new Between(lower, upper);
	}
	
	public static Between values(double lower, double upper, double epsilon) {
		return new Between(lower, upper, epsilon);
	}
	
	public static Between values(String name, double lower, double upper) {
		return new Between(name, lower, upper);
	}
	
	public static Between values(String name, double lower, double upper, double epsilon) {
		return new Between(name, lower, upper, epsilon);
	}

}