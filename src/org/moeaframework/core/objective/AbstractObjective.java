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
package org.moeaframework.core.objective;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.moeaframework.core.Constructable;

public abstract class AbstractObjective implements Objective {
	
	private static final long serialVersionUID = 8819865234325786924L;
	
	protected final String name;
	
	protected double value;
	
	public AbstractObjective() {
		this(null);
	}
	
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