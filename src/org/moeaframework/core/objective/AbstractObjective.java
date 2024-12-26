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
package org.moeaframework.core.objective;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.moeaframework.core.Constructable;

/**
 * Abstract class for implementing objectives.  The objective value is initialized to {@value Double#NaN}, which
 * serves as an indicator that the objective has not been assigned.
 * <p>
 * Objectives can be assigned a name, but if unset will derive its name from its current index by calling
 * {@link Objective#getNameOrDefault(Objective, int)}.  Such objectives are also called "anonymous".
 */
public abstract class AbstractObjective implements Objective {
	
	private static final long serialVersionUID = 8819865234325786924L;
	
	/**
	 * The objective name, or {@code null} if anonymous.
	 */
	protected final String name;
	
	/**
	 * The objective value.
	 */
	protected double value;
	
	/**
	 * Constructs a new, anonymous objective.
	 * 
	 */
	public AbstractObjective() {
		this(null);
	}
	
	/**
	 * Constructs a new objective with the given name.
	 * 
	 * @param name the objective name, or {@code null} to create an anonymous objective
	 */
	protected AbstractObjective(String name) {
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
	 * Updates this objective with the given objective value and returns itself.
	 * 
	 * @param value the objective value
	 * @return a reference to this objective
	 */
	public Objective withValue(double value) {
		setValue(value);
		return this;
	}
	
	@Override
	public String getDefinition() {
		if (name == null) {
			return Constructable.createDefinition(Objective.class, getClass());
		} else {
			return Constructable.createDefinition(Objective.class, getClass(), name);
		}
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
			AbstractObjective rhs = (AbstractObjective)obj;
			
			return new EqualsBuilder()
					.append(name, rhs.name)
					.append(value, rhs.value)
					.isEquals();
		}
	}
	
}
