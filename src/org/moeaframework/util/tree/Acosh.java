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
 * The node for calculating the hyperbolic arc cosine of a number.
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
 *     <td>The hyperbolic arc cosine of the angle</td>
 *   </tr>
 * </table>
 * 
 * @see Math#cosh(double)
 */
public class Acosh extends Node {
	
	private static final long serialVersionUID = 6371412623094117311L;

	/**
	 * Constructs a new node for calculating the hyperbolic arc cosine of a number.
	 */
	public Acosh() {
		super(Number.class, Number.class);
	}
	
	@Override
	public Acosh copyNode() {
		return new Acosh();
	}
	
	@Override
	public Number evaluate(Environment environment) {
		return NumberArithmetic.acosh((Number)getArgument(0).evaluate(environment));
	}

}
