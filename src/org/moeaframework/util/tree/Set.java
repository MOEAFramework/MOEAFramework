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
 * The node for assigning the value of a named variable within the current
 * scope.  Variables are locally-scoped within functions (i.e., {@link Define}
 * and {@link Lambda}) and globally-scoped elsewhere.  The inputs and
 * outputs to this node are shown below:
 * 
 * <table border="1" cellpadding="3" cellspacing="0">
 *   <tr class="TableHeadingColor">
 *     <th width="25%" align="left">Name</th>
 *     <th width="25%" align="left">Type</th>
 *     <th width="50%" align="left">Description</th>
 *   </tr>
 *   <tr>
 *     <td>Argument 1</td>
 *     <td>User-Defined</td>
 *     <td>The value to store in the named variable</td>
 *   </tr>
 *   <tr>
 *     <td>Return Value</td>
 *     <td>User-Defined</td>
 *     <td>The value stored in the named variable</td>
 *   </tr>
 * </table>
 * 
 * @see Get
 */
public class Set extends Node {
	
	/**
	 * The name of the variable.
	 */
	private final String name;
	
	/**
	 * Constructs a new node for assigning the value of a named variable within
	 * the current scope.
	 * 
	 * @param type the type of the variable
	 * @param name the name of the variable
	 */
	public Set(Class<?> type, String name) {
		super(type, type);
		this.name = name;
	}

	@Override
	public Set copyNode() {
		return new Set(getArgumentType(0), name);
	}

	@Override
	public Object evaluate(Environment environment) {
		Object value = getArgument(0).evaluate(environment);
		environment.set(name, value);
		return value;
	}

}
