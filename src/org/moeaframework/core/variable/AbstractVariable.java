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
package org.moeaframework.core.variable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Abstract class for implementing variables.
 * <p>
 * Variables can be assigned a name, but if unset will derive its name from its current index by calling
 * {@link Variable#getNameOrDefault(Variable, int)}.  Such variables are also called "anonymous".
 */
public abstract class AbstractVariable implements Variable {

	private static final long serialVersionUID = 1032891207461956713L;
	
	/**
	 * The variable name, or {@code null} if anonymous.
	 */
	protected final String name;
	
	/**
	 * Constructs a new, anonymous variable.
	 */
	public AbstractVariable() {
		this(null);
	}
	
	/**
	 * Constructs a new variable with the given name.
	 * 
	 * @param name the variable name, or {@code null} to create an anonymous variable
	 */
	public AbstractVariable(String name) {
		super();
		this.name = name;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(name)
				.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if ((obj == null) || (obj.getClass() != getClass())) {
			return false;
		} else {
			AbstractVariable rhs = (AbstractVariable)obj;
			
			return new EqualsBuilder()
					.append(name, rhs.name)
					.isEquals();
		}
	}

}
