/* Copyright 2009-2018 David Hadka
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
package org.moeaframework.util.tree;

/**
 * The node for reading the value stored in a named variable within the current
 * scope.  See {@link Set} for details on scoping.  The inputs and outputs to
 * this node are shown below:
 * <p>
 * If the named variable has not yet been set, a default value is returned
 * depending on the return type.  For numeric values, the default is
 * {@code 0}.  For boolean values, the default is {@code false}.  For objects,
 * the default is {@code null}.
 * 
 * <table border="1" cellpadding="3" cellspacing="0">
 *   <tr class="TableHeadingColor">
 *     <th width="25%" align="left">Name</th>
 *     <th width="25%" align="left">Type</th>
 *     <th width="50%" align="left">Description</th>
 *   </tr>
 *   <tr>
 *     <td>Return Value</td>
 *     <td>User-Defined</td>
 *     <td>The valued stored in the named variable</td>
 *   </tr>
 * </table>
 * 
 * @see Set
 */
public class Get extends Node {
	
	/**
	 * The name of the variable.
	 */
	private final String name;
	
	/**
	 * Constructs a new node for reading the value stored in a named variable
	 * within the current scope.
	 * 
	 * @param type the type of the variable
	 * @param name the name of the variable
	 */
	public Get(Class<?> type, String name) {
		super(type);
		this.name = name;
	}

	@Override
	public Get copyNode() {
		return new Get(getReturnType(), name);
	}

	@Override
	public Object evaluate(Environment environment) {
		Object value = environment.get(getReturnType(), name);
		
		if (value == null) {
			value = getDefaultValue();
		}
		
		return value;
	}
	
	/**
	 * Returns the default value for the return type of this node.
	 * 
	 * @return the default value for the return type of this node
	 */
	public Object getDefaultValue() {
		if (getReturnType().equals(Byte.class) ||
				getReturnType().equals(Short.class) ||
				getReturnType().equals(Integer.class) || 
				getReturnType().equals(Long.class)) {
			return 0;
		} else if (getReturnType().equals(Float.class) ||
				getReturnType().equals(Double.class)) {
			return 0.0;
		} else if (getReturnType().equals(Number.class)) {
			return 0;
		} else if (getReturnType().equals(Boolean.class)) {
			return false;
		} else {
			return null;
		}
	}
	
	public String toString() {
		return name;
	}

}
