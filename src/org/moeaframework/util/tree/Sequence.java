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
 * The node for executing two or more expressions in sequence.  The return
 * value of the last expression is returned, and the return type should be
 * specified using an appropriate constructor.  Since the expressions are
 * strongly typed, the type(s) specified in the constructor is important.  The
 * inputs and outputs to this node are shown below:
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
 *     <td>The first expression</td>
 *   </tr>
 *   <tr>
 *     <td>Argument 2</td>
 *     <td>User-Defined</td>
 *     <td>The second expression</td>
 *   </tr>
 *   <tr>
 *     <td>Return Value</td>
 *     <td>User-Defined</td>
 *     <td>The return value of the last expression</td>
 *   </tr>
 * </table>
 */
public class Sequence extends Node {
	
	/**
	 * Constructs a new node for executing two expressions in sequence.
	 */
	public Sequence() {
		this(Object.class);
	}
	
	/**
	 * Constructs a new node for executing two expressions in sequence.
	 * 
	 * @param type the return type of the last expression to be executed
	 */
	public Sequence(Class<?> type) {
		this(Object.class, type);
	}
	
	/**
	 * Constructs a new node for executing two expressions in sequence.
	 * 
	 * @param type1 the return type of the first expression
	 * @param type2 the return type of the second expression, which is also the
	 *        return type of this sequence
	 */
	public Sequence(Class<?> type1, Class<?> type2) {
		super(type2, type1, type2);
	}
	
	/**
	 * Constructs a new node for executing a specified number of expressions
	 * in sequence.
	 * 
	 * @param types the types of each expression in this sequence
	 */
	public Sequence(Class<?>... types) {
		super(types[types.length-1], types);
	}

	@Override
	public Sequence copyNode() {
		return new Sequence(getArgumentTypes());
	}

	@Override
	public Object evaluate(Environment environment) {
		getArgument(0).evaluate(environment);
		return getArgument(1).evaluate(environment);
	}

}
