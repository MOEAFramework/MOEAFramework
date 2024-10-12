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
import org.moeaframework.core.Settings;
import org.moeaframework.util.DefinedType;

public abstract class BoundedConstraint extends AbstractConstraint {

	private static final long serialVersionUID = -2766984574651872793L;
	
	protected final double lower;
	
	protected final double upper;
	
	protected final double epsilon;

	public BoundedConstraint(double lower, double upper) {
		this(lower, upper, Settings.EPS);
	}
	
	public BoundedConstraint(double lower, double upper, double epsilon) {
		super();
		this.lower = lower;
		this.upper = upper;
		this.epsilon = epsilon;
	}
	
	public BoundedConstraint(BoundedConstraint copy) {
		this(copy.lower, copy.upper, copy.epsilon);
		this.value = copy.value;
	}
	
	public double getLower() {
		return lower;
	}
	
	public double getUpper() {
		return upper;
	}
	
	public double getEpsilon() {
		return epsilon;
	}
	
	@Override
	public String getDefinition() {
		return DefinedType.createDefinition(Constraint.class, getClass(), lower, upper, epsilon);
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
