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

import java.io.Serializable;

import org.moeaframework.core.FrameworkException;

public interface Constraint extends Comparable<Constraint>, Serializable {
		
	public double getValue();
	
	public void setValue(double value);
	
	/**
	 * Returns the magnitude of constraint violation as a non-negative number, with {@value #SATISFIED} representing
	 * satisfied or feasible constraints.  When comparing two constraints, smaller magnitudes are considered better.
	 * 
	 * @return the magnitude of constraint violation
	 */
	public double getMagnitudeOfViolation();
	
	public Constraint copy();
	
	public default boolean isViolation() {
		return getMagnitudeOfViolation() != 0.0;
	}

	@Override
	public default int compareTo(Constraint other) {
		if (getClass() != other.getClass()) {
			throw new FrameworkException("unable to compare constraint values between " + getClass().getSimpleName() +
					" and " + other.getClass().getSimpleName());
		}
		
		return Double.compare(getMagnitudeOfViolation(), other.getMagnitudeOfViolation());
	}
	
	public static Constraint createDefault() {
		return new Equal(0.0);
	}
	
}
