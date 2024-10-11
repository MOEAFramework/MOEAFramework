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

public abstract class AbstractConstraint implements Constraint {

	private static final long serialVersionUID = -9233514055091031L;
	
	protected double value;
			
	public AbstractConstraint() {
		super();
		
		// TODO: For consistency with older versions, constraints default to 0.0 instead of NaN.  However, it could be
		// useful to default to NaN and warn if used, as that should indicate a user error.
		this.value = 0.0; //Double.NaN;
	}
	
	public void setValue(double value) {
		this.value = value;
	}
	
	public double getValue() {
		return value;
	}
	
	public Constraint withValue(double value) {
		setValue(value);
		return this;
	}
	
	public abstract double getMagnitudeOfViolation();
	
	public abstract Constraint copy();
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" +  value + ")";
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(value)
				.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if ((obj == null) || (obj.getClass() != getClass())) {
			return false;
		} else {
			AbstractConstraint rhs = (AbstractConstraint)obj;
			
			return new EqualsBuilder()
					.append(value, rhs.value)
					.isEquals();
		}
	}

}
