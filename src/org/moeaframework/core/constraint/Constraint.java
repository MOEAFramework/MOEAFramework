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

import java.io.Serializable;

import org.moeaframework.core.Copyable;
import org.moeaframework.core.Defined;
import org.moeaframework.core.Named;
import org.moeaframework.core.TypeMismatchException;

/**
 * Defines a constraint.  While all constraints are assigned a value, the interpretation of that value with respect to
 * feasibility depends on the specific constraint type.  Thus, callers should prefer using specific methods provided
 * by this interface than checking the value itself.
 */
public interface Constraint extends Comparable<Constraint>, Copyable<Constraint>, Serializable, Defined, Named {
	
	/**
	 * Returns the value of this constraint.
	 * 
	 * @return the value of this constraint
	 */
	public double getValue();
	
	/**
	 * Sets the value of this constraint.
	 * 
	 * @param value the value of this constraint
	 */
	public void setValue(double value);
	
	/**
	 * Sets the value of this constraint and returns this instance.
	 * 
	 * @param value the value of this constraint
	 * @return this instance
	 */
	public default Constraint withValue(double value) {
		setValue(value);
		return this;
	}
	
	/**
	 * Returns the magnitude of constraint violation as a non-negative number, with {@code 0.0} representing
	 * satisfied or feasible constraints.  When comparing two constraints, smaller magnitudes are considered better.
	 * 
	 * @return the magnitude of constraint violation
	 */
	public double getMagnitudeOfViolation();
	
	/**
	 * Returns {@code true} if this constraint is violated; {@code false} otherwise.
	 * 
	 * @return {@code true} if this constraint is violated; {@code false} otherwise
	 */
	public default boolean isViolation() {
		return getMagnitudeOfViolation() != 0.0;
	}

	@Override
	public default int compareTo(Constraint other) {
		if (getClass() != other.getClass()) {
			throw TypeMismatchException.notComparable(getClass(), other.getClass());
		}
		
		return Double.compare(getMagnitudeOfViolation(), other.getMagnitudeOfViolation());
	}
	
	/**
	 * Returns a new instance of the default constraint.  This is the constraint type used if not explicitly
	 * configured by the problem or user.
	 * 
	 * @return the default constraint
	 */
	public static Constraint createDefault() {
		return new Equal(0.0);
	}
	
	/**
	 * Returns the name of the constraint, using either the name assigned to the constraint or deriving the name from
	 * its index.
	 * 
	 * @param constraint the constraint
	 * @param index the index of the constraint
	 * @return the name of the constraint
	 */
	public static String getNameOrDefault(Constraint constraint, int index) {
		return constraint.getName() == null ? "Constr" + (index + 1) : constraint.getName();
	}
	
}
