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
package org.moeaframework.core.attribute;

import org.moeaframework.core.Solution;

/**
 * Operator index attribute.  This is used by {@link org.moeaframework.core.operator.AdaptiveMultimethodVariation} to
 * indicate which operator, based on its index, produced the solution.
 */
public final class OperatorIndex implements Attribute {
	
	/**
	 * The name / key used for this attribute.
	 */
	public static final String ATTRIBUTE_NAME = "operator";

	private OperatorIndex() {
		super();
	}
	
	/**
	 * Returns {@code true} if the solution defines this attribute; {@code false} otherwise.
	 * 
	 * @param solution the solution
	 * @return {@code true} if the solution defines this attribute; {@code false} otherwise
	 */
	public static final boolean hasAttribute(Solution solution) {
		return solution.hasAttribute(ATTRIBUTE_NAME);
	}
	
	/**
	 * Sets the value of the attribute on the solution.
	 * 
	 * @param solution the solution
	 * @param value the value to set
	 */
	public static final void setAttribute(Solution solution, int value) {
		solution.setAttribute(ATTRIBUTE_NAME, value);
	}
	
	/**
	 * Returns the value of the attribute stored in the solution.
	 * 
	 * @param solution the solution
	 * @return the stored value
	 */
	public static final int getAttribute(Solution solution) {
		return (Integer)solution.getAttribute(ATTRIBUTE_NAME);
	}
	
	/**
	 * Removes the attribute from the solution.
	 * 
	 * @param solution the solution
	 */
	public static final void removeAttribute(Solution solution) {
		solution.removeAttribute(ATTRIBUTE_NAME);
	}

}
