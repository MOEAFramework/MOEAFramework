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
package org.moeaframework.util.tree;

/**
 * The node for comparing if a number is less than or equal to another.
 * 
 * <table style="margin-top: 1em; width: 100%">
 *   <caption style="text-align: left">Node signature:</caption>
 *   <tr>
 *     <th style="width: 15%; text-align: left">Name</th>
 *     <th style="width: 15%; text-align: left">Type</th>
 *     <th style="width: 70%; text-align: left">Description</th>
 *   </tr>
 *   <tr>
 *     <td>Argument 1</td>
 *     <td>Number</td>
 *     <td>The first number</td>
 *   </tr>
 *   <tr>
 *     <td>Argument 2</td>
 *     <td>Number</td>
 *     <td>The second number</td>
 *   </tr>
 *   <tr>
 *     <td>Return Value</td>
 *     <td>Boolean</td>
 *     <td>{@code true} if the first number is less than or equal to the second; {@code false} otherwise</td>
 *   </tr>
 * </table>
 */
public class LessThanOrEqual extends Node {
	
	private static final long serialVersionUID = 3427747921710390373L;

	/**
	 * Constructs a new node for comparing if a number is less than or equal to another.
	 */
	public LessThanOrEqual() {
		super(Boolean.class, Number.class, Number.class);
	}

	@Override
	public LessThanOrEqual copyNode() {
		return new LessThanOrEqual();
	}

	@Override
	public Boolean evaluate(Environment environment) {
		return NumberArithmetic.lessThanOrEqual(
				(Number)getArgument(0).evaluate(environment),
				(Number)getArgument(1).evaluate(environment));
	}

}
