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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.moeaframework.core.Constructable;
import org.moeaframework.core.Settings;

/**
 * Abstract class for bounded constraints of the form {@code <lower> <op> <value> <op> <upper>}.  To handle numerical
 * precision when comparing floating-point values, a small epsilon difference is permitted, which by default is
 * {@link Settings#EPS}.
 */
public abstract class BoundedConstraint extends AbstractConstraint {

	private static final long serialVersionUID = -2766984574651872793L;
	
	/**
	 * The lower bound of this constraint.
	 */
	protected final double lower;
	
	/**
	 * The upper bound of this constraint.
	 */
	protected final double upper;
	
	/**
	 * The epsilon or numerical precision of this constraint.
	 */
	protected final double epsilon;

	/**
	 * Constructs a new bounded constraint.
	 * 
	 * @param lower the lower threshold
	 * @param upper the upper threshold
	 */
	public BoundedConstraint(double lower, double upper) {
		this(null, lower, upper);
	}
	
	/**
	 * Constructs a new bounded constraint.
	 * 
	 * @param lower the lower threshold
	 * @param upper the upper threshold
	 * @param epsilon the epsilon value
	 */
	public BoundedConstraint(double lower, double upper, double epsilon) {
		this(null, lower, upper, epsilon);
	}
	
	/**
	 * Constructs a new bounded constraint.
	 * 
	 * @param name the name
	 * @param lower the lower threshold
	 * @param upper the upper threshold
	 */
	public BoundedConstraint(String name, double lower, double upper) {
		this(name, lower, upper, Settings.EPS);
	}
	
	/**
	 * Constructs a new bounded constraint.
	 * 
	 * @param name the name
	 * @param lower the lower threshold
	 * @param upper the upper threshold
	 * @param epsilon the epsilon value
	 */
	public BoundedConstraint(String name, double lower, double upper, double epsilon) {
		super(name);
		this.lower = lower;
		this.upper = upper;
		this.epsilon = epsilon;
	}
	
	/**
	 * Constructs a copy of a bounded constraint.
	 * 
	 * @param copy the constraint to copy
	 */
	public BoundedConstraint(BoundedConstraint copy) {
		this(copy.name, copy.lower, copy.upper, copy.epsilon);
		this.value = copy.value;
	}
	
	/**
	 * Returns the lower bound of this constraint.
	 * 
	 * @return the lower bound
	 */
	public double getLower() {
		return lower;
	}
	
	/**
	 * Returns the upper bound of this constraint.
	 * 
	 * @return the upper bound
	 */
	public double getUpper() {
		return upper;
	}
	
	/**
	 * Returns the epsilon or numerical precision of this constraint.
	 * 
	 * @return the epsilon value
	 */
	public double getEpsilon() {
		return epsilon;
	}
	
	@Override
	public String getDefinition() {
		if (name == null) {
			return Constructable.createDefinition(Constraint.class, getClass(), lower, upper, epsilon);
		} else {
			return Constructable.createDefinition(Constraint.class, getClass(), name, lower, upper, epsilon);
		}
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" + value + ",lower=" + lower + ",upper=" + upper + ")";
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.appendSuper(super.hashCode())
				.append(lower)
				.append(upper)
				.append(epsilon)
				.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if ((obj == null) || (obj.getClass() != getClass())) {
			return false;
		} else {
			BoundedConstraint rhs = (BoundedConstraint)obj;
			
			return new EqualsBuilder()
					.appendSuper(super.equals(obj))
					.append(lower, rhs.lower)
					.append(upper, rhs.upper)
					.append(epsilon, rhs.epsilon)
					.isEquals();
		}
	}

}
