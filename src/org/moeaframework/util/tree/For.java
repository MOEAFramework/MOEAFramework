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
 * The node for executing an expression for a given number of iterations.  A
 * named variable is created that contains the loop counter.  The inputs and
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
 *     <td>Number</td>
 *     <td>The starting loop counter value</td>
 *   </tr>
 *   <tr>
 *     <td>Argument 2</td>
 *     <td>Number</td>
 *     <td>The ending loop counter value</td>
 *   </tr>
 *   <tr>
 *     <td>Argument 3</td>
 *     <td>Number</td>
 *     <td>The increment of the loop counter</td>
 *   </tr>
 *   <tr>
 *     <td>Argument 4</td>
 *     <td>User-Defined</td>
 *     <td>The expression to execute inside the loop</td>
 *   </tr>
 *   <tr>
 *     <td>Return Value</td>
 *     <td>User-Defined</td>
 *     <td>The return value of the last executed expression; or {@code null}
 *         if the loop was never executed</td>
 *   </tr>
 * </table>
 */
public class For extends Node {
	
	/**
	 * The name of the variable where the loop counter is stored
	 */
	private final String variableName;
	
	/**
	 * Constructs a new node for executing an expression for a given number
	 * of iterations.
	 * 
	 * @param variableName the name of the variable where the loop counter is
	 *        stored
	 */
	public For(String variableName) {
		super(Object.class, Number.class, Number.class, Number.class,
				Object.class);
		this.variableName = variableName;
	}

	@Override
	public For copyNode() {
		return new For(variableName);
	}

	@Override
	public Object evaluate(Environment environment) {
		Number start = (Number)getArgument(0).evaluate(environment);
		Number end = (Number)getArgument(1).evaluate(environment);
		Number step = (Number)getArgument(2).evaluate(environment);
		Object value = null;
		
		for (Number i = start; NumberArithmetic.lessThan(i, end);
				i = NumberArithmetic.add(i, step)) {
			environment.set(variableName, i);
			value = getArgument(3).evaluate(environment);
		}
		
		return value;
	}

}
