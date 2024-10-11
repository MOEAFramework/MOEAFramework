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

public class Outside extends BoundedConstraint {

	private static final long serialVersionUID = 7023737068172462391L;

	public Outside(double lower, double upper) {
		super(lower, upper);
	}
	
	public Outside(double lower, double upper, double epsilon) {
		super(lower, upper, epsilon);
	}
	
	public Outside(Outside copy) {
		super(copy);
	}

	@Override
	public double getMagnitudeOfViolation() {
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
	
	public static Outside values(double lower, double upper) {
		return new Outside(lower, upper);
	}
	
	public static Outside values(double lower, double upper, double epsilon) {
		return new Outside(lower, upper, epsilon);
	}

}
