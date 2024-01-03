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
package org.moeaframework.util.tree;

/**
 * The node for calculating the trigonometric tangent of an angle specified in
 * radians.
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
 *     <td>The angle</td>
 *   </tr>
 *   <tr>
 *     <td>Return Value</td>
 *     <td>Number</td>
 *     <td>The tangent of the angle</td>
 *   </tr>
 * </table>
 * 
 * @see Math#tan(double)
 */
public class Tan extends Node {
	
	/**
	 * Constructs a new node for calculating the trigonometric tangent of an
	 * angle specified in radians.
	 */
	public Tan() {
		super(Number.class, Number.class);
	}
	
	@Override
	public Tan copyNode() {
		return new Tan();
	}
	
	@Override
	public Number evaluate(Environment environment) {
		return NumberArithmetic.tan(
				(Number)getArgument(0).evaluate(environment));
	}

}
