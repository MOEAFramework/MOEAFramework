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
 * The node for executing one of two expressions depending on the result of a
 * boolean expression.  The inputs and outputs to this node are shown below:
 * 
 * <table border="1" cellpadding="3" cellspacing="0">
 *   <tr class="TableHeadingColor">
 *     <th width="25%" align="left">Name</th>
 *     <th width="25%" align="left">Type</th>
 *     <th width="50%" align="left">Description</th>
 *   </tr>
 *   <tr>
 *     <td>Argument 1</td>
 *     <td>Boolean</td>
 *     <td>The condition that decides which expression to execute</td>
 *   </tr>
 *   <tr>
 *     <td>Argument 2</td>
 *     <td>User-Defined</td>
 *     <td>The expression to execute when the condition is {@code true}</td>
 *   </tr>
 *   <tr>
 *     <td>Argument 3</td>
 *     <td>User-Defined</td>
 *     <td>The expression to execute when the condition is {@code false}</td>
 *   </tr>
 *   <tr>
 *     <td>Return Value</td>
 *     <td>User-Defined</td>
 *     <td>The return value of the executed expression</td>
 *   </tr>
 * </table>
 */
public class IfElse extends Node {
	
	/**
	 * Constructs a new node for executing one of two expressions depending on
	 * the result of a boolean expression.
	 */
	public IfElse() {
		this(Object.class);
	}
	
	/**
	 * Constructs a new node for executing one of two expressions depending on
	 * the result of a boolean expression.
	 * 
	 * @param type the return type of each expression, which is also the return
	 *        type of this node
	 */
	public IfElse(Class<?> type) {
		super(type, Boolean.class, type, type);
	}

	@Override
	public IfElse copyNode() {
		return new IfElse(getReturnType());
	}

	@Override
	public Object evaluate(Environment environment) {
		Boolean condition = (Boolean)getArgument(0).evaluate(environment);
		
		if (condition) {
			return getArgument(1).evaluate(environment);
		} else {
			return getArgument(2).evaluate(environment);
		}
	}

}
