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

/**
 * Abstract class for implementing constraints.  The constraint value is initialized to {@value Double#NaN}, which
 * serves as an indicator the constraint has not been assigned.  By convention, implementations should consider this
 * default value to be feasible.
 * <p>
 * Constraints can be assigned a name, but if unset will derive its name from its current index by calling
 * {@link Constraint#getNameOrDefault(Constraint, int)}.  Such constraints are also called "anonymous".
 */
public abstract class AbstractConstraint implements Constraint {

	private static final long serialVersionUID = -9233514055091031L;
	
	/**
	 * The constraint name, or {@code null} if anonymous.
	 */
	protected final String name;
	
	/**
	 * The constraint value.
	 */
	protected double value;
	
	/**
	 * Constructs a new, anonymous constraint.
	 */
	public AbstractConstraint() {
		this(null);
	}
	
	/**
	 * Constructs a new constraint with the given name.
	 * 
	 * @param name the constraint name, or {@code null} to create an anonymous constraint
	 */
	public AbstractConstraint(String name) {
		super();
		this.name = name;
		this.value = Double.NaN;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public void setValue(double value) {
		this.value = value;
	}
	
	@Override
	public double getValue() {
		return value;
	}
	
	/**
	 * Updates this constraint with the given constraint value and returns itself.
	 * 
	 * @param value the constraint value
	 * @return a reference to this constraint
	 */
	@Override
	public Constraint withValue(double value) {
		setValue(value);
		return this;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "(" +  value + ")";
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(name)
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
					.append(name,  rhs.name)
					.append(value, rhs.value)
					.isEquals();
		}
	}

}
