/* Copyright 2009-2022 David Hadka
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
 * The node for truncating, or bounding, a number within a range.  If the
 * number is outside the defined range, the number is set to the nearest number
 * within the range.
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
 *     <td>The number to be truncated</td>
 *   </tr>
 *   <tr>
 *     <td>Return Value</td>
 *     <td>Number</td>
 *     <td>The value of the number truncated to reside within a given range</td>
 *   </tr>
 * </table>
 */
public class Truncate extends Node {
	
	/**
	 * The minimum value in the range.
	 */
	private final double min;
	
	/**
	 * The maximum value in the range.
	 */
	private final double max;
	
	/**
	 * Constructs a new node for truncating a number within a range.
	 * 
	 * @param min the minimum value in the range
	 * @param max the maximum value in the range
	 */
	public Truncate(double min, double max) {
		super(Number.class, Number.class);
		this.min = min;
		this.max = max;
	}
	
	@Override
	public Truncate copyNode() {
		return new Truncate(min, max);
	}
	
	@Override
	public Number evaluate(Environment environment) {
		Number value = (Number)getArgument(0).evaluate(environment);
		
		if (NumberArithmetic.lessThan(value, min)) {
			return min;
		} else if (NumberArithmetic.greaterThan(value, max)) {
			return max;
		} else {
			return value;
		}
	}

}
