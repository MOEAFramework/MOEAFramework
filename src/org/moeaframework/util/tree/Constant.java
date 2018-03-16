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
 * The node for defining a constant value.  The inputs and outputs to this node
 * are shown below:
 * 
 * <table border="1" cellpadding="3" cellspacing="0">
 *   <tr class="TableHeadingColor">
 *     <th width="25%" align="left">Name</th>
 *     <th width="25%" align="left">Type</th>
 *     <th width="50%" align="left">Description</th>
 *   </tr>
 *   <tr>
 *     <td>Return Value</td>
 *     <td>Number</td>
 *     <td>The constant value</td>
 *   </tr>
 * </table>
 */
public class Constant extends Node {
	
	/**
	 * The value.
	 */
	private final Object value;
	
	/**
	 * Constructs a new node for defining a constant floating-point number.
	 * 
	 * @param value the floating-point number
	 */
	public Constant(double value) {
		this(Double.class, value);
	}
	
	/**
	 * Constructs a new node for defining a constant integer value.
	 * 
	 * @param value the integer value
	 */
	public Constant(long value) {
		this(Long.class, value);
	}
	
	/**
	 * Constructs a new node for defining a constant boolean value.
	 * 
	 * @param value the boolean value
	 */
	public Constant(boolean value) {
		this(Boolean.class, value);
	}
	
	/**
	 * Constructs a new node for defining a constant {@link Object}.  It is
	 * necessary to pass the type explicitly to ensure values such as 
	 * {@code null} are handled correctly.
	 * 
	 * @param type the type of the object
	 * @param value the object
	 */
	public Constant(Class<?> type, Object value) {
		super(type);
		this.value = value;
	}
	
	@Override
	public Constant copyNode() {
		return new Constant(getReturnType(), value);
	}
	
	@Override
	public Object evaluate(Environment environment) {
		return value;
	}
	
	@Override
	public String toString() {
		return String.valueOf(value);
	}

}
