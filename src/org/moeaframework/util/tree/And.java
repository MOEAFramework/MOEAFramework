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
 * The node for calculating the logical AND of two boolean values.
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
 *     <td>Boolean</td>
 *     <td>The first boolean value</td>
 *   </tr>
 *   <tr>
 *     <td>Argument 2</td>
 *     <td>Boolean</td>
 *     <td>The second boolean value</td>
 *   </tr>
 *   <tr>
 *     <td>Return Value</td>
 *     <td>Number</td>
 *     <td>The logical AND of the two boolean values</td>
 *   </tr>
 * </table>
 */
public class And extends Node {

	private static final long serialVersionUID = -174601363499585162L;

	/**
	 * Constructs a new node for calculating the logical AND of two boolean values.
	 */
	public And() {
		super(Boolean.class, Boolean.class, Boolean.class);
	}

	@Override
	public And copyNode() {
		return new And();
	}

	@Override
	public Boolean evaluate(Environment environment) {
		return (Boolean)getArgument(0).evaluate(environment) && (Boolean)getArgument(1).evaluate(environment);
	}

}
