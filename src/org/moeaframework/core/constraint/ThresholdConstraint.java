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

public abstract class ThresholdConstraint extends AbstractConstraint {
	
	private static final long serialVersionUID = -6158520548738752673L;

	protected final double threshold;
	
	protected final double epsilon;
		
	public ThresholdConstraint(double threhsold) {
		this(threhsold, Settings.EPS);
	}
	
	public ThresholdConstraint(double threshold, double epsilon) {
		super();
		this.threshold = threshold;
		this.epsilon = epsilon;
	}
	
	public ThresholdConstraint(ThresholdConstraint copy) {
		this(copy.threshold, copy.epsilon);
		this.value = copy.value;
	}
	
	public double getThreshold() {
		return threshold;
	}
	
	public double getEpsilon() {
		return epsilon;
	}
	
	@Override
	public String getDefinition() {
		return Constructable.createDefinition(Constraint.class, getClass(), threshold, epsilon);
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
