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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.moeaframework.core.Constructable;
import org.moeaframework.core.Settings;

/**
 * Abstract class for constraints of the form {@code c <op> <threshold>}.  To handle numerical precision when comparing
 * floating-point values, a small epsilon difference is permitted, which by default is {@link Settings#EPS}.
 */
public abstract class ThresholdConstraint extends AbstractConstraint {
	
	private static final long serialVersionUID = -6158520548738752673L;

	protected final double threshold;
	
	protected final double epsilon;
	
	/**
	 * Constructs a new threshold constraint.
	 * 
	 * @param threhsold the threshold value
	 */
	public ThresholdConstraint(double threhsold) {
		this(null, threhsold);
	}
	
	/**
	 * Constructs a new threshold constraint.
	 * 
	 * @param threshold the threshold value
	 * @param epsilon the epsilon value
	 */
	public ThresholdConstraint(double threshold, double epsilon) {
		this(null, threshold, epsilon);
	}
	
	/**
	 * Constructs a new threshold constraint.
	 * 
	 * @param name the name
	 * @param threhsold the threshold value
	 */
	public ThresholdConstraint(String name, double threhsold) {
		this(name, threhsold, Settings.EPS);
	}
	
	/**
	 * Constructs a new threshold constraint.
	 * 
	 * @param name the name
	 * @param threshold the threshold value
	 * @param epsilon the epsilon value
	 */
	public ThresholdConstraint(String name, double threshold, double epsilon) {
		super(name);
		this.threshold = threshold;
		this.epsilon = epsilon;
	}
	
	/**
	 * Constructs a copy of a threshold constraint.
	 * 
	 * @param copy the copy
	 */
	public ThresholdConstraint(ThresholdConstraint copy) {
		this(copy.name, copy.threshold, copy.epsilon);
		this.value = copy.value;
	}
	
	/**
	 * Returns the threshold value.
	 * 
	 * @return the threshold value
	 */
	public double getThreshold() {
		return threshold;
	}
	
	/**
	 * Returns the epsilon value used for numeric precision.
	 * 
	 * @return the epsilon value
	 */
	public double getEpsilon() {
		return epsilon;
	}
	
	@Override
	public String getDefinition() {
		if (name == null) {
			return Constructable.createDefinition(Constraint.class, getClass(), threshold, epsilon);
		} else {
			return Constructable.createDefinition(Constraint.class, getClass(), name, threshold, epsilon);
		}
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" +  value + ",threshold=" + threshold + ")";
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.appendSuper(super.hashCode())
				.append(threshold)
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
			ThresholdConstraint rhs = (ThresholdConstraint)obj;
			
			return new EqualsBuilder()
					.appendSuper(super.equals(obj))
					.append(threshold, rhs.threshold)
					.append(epsilon, rhs.epsilon)
					.isEquals();
		}
	}

}
