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
 * The equality constraint, or {@code c == <threshold>}.
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
	 * Constructs a copy of an equality constraint.
	 * 
	 * @param copy the copy
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
	
	public static Equal to(double threshold) {
		return new Equal(threshold);
	}
	
	public static Equal to(double threshold, double epsilon) {
		return new Equal(threshold, epsilon);
	}

}
