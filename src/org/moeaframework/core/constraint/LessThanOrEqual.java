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
 * The less than or equal to constraint, or {@code c <= <threshold>}.
 */
public class LessThanOrEqual extends ThresholdConstraint {

	private static final long serialVersionUID = -2950482083962039249L;

	public LessThanOrEqual(double threshold) {
		super(threshold);
	}
	
	public LessThanOrEqual(double threshold, double epsilon) {
		super(threshold, epsilon);
	}
	
	public LessThanOrEqual(String name, double threshold) {
		super(name, threshold);
	}
	
	public LessThanOrEqual(String name, double threshold, double epsilon) {
		super(name, threshold, epsilon);
	}
	
	public LessThanOrEqual(LessThanOrEqual copy) {
		super(copy);
	}

	@Override
	public double getMagnitudeOfViolation() {
		if (Double.isNaN(value)) {
			return 0.0;
		}
		
		double diff = Math.abs(value - threshold);
		return value <= threshold || diff <= epsilon ? 0.0 : diff;
	}

	@Override
	public LessThanOrEqual copy() {
		return new LessThanOrEqual(this);
	}
	
	public static LessThanOrEqual to(double threshold) {
		return new LessThanOrEqual(threshold);
	}
	
	public static LessThanOrEqual to(double threshold, double epsilon) {
		return new LessThanOrEqual(threshold, epsilon);
	}
	
	public static LessThanOrEqual to(String name, double threshold) {
		return new LessThanOrEqual(name, threshold);
	}
	
	public static LessThanOrEqual to(String name, double threshold, double epsilon) {
		return new LessThanOrEqual(name, threshold, epsilon);
	}

}
