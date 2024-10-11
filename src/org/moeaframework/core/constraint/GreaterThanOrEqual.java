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

public class GreaterThanOrEqual extends ThresholdConstraint {

	private static final long serialVersionUID = 132924592516674872L;

	public GreaterThanOrEqual(double threshold) {
		super(threshold);
	}
	
	public GreaterThanOrEqual(double threshold, double epsilon) {
		super(threshold, epsilon);
	}
	
	public GreaterThanOrEqual(GreaterThanOrEqual copy) {
		super(copy);
	}

	@Override
	public double getMagnitudeOfViolation() {
		double diff = Math.abs(value - threshold);
		return value >= threshold || diff <= epsilon ? 0.0 : diff;
	}

	@Override
	public GreaterThanOrEqual copy() {
		return new GreaterThanOrEqual(this);
	}
	
	public static GreaterThanOrEqual to(double threshold) {
		return new GreaterThanOrEqual(threshold);
	}
	
	public static GreaterThanOrEqual to(double threshold, double epsilon) {
		return new GreaterThanOrEqual(threshold, epsilon);
	}

}
