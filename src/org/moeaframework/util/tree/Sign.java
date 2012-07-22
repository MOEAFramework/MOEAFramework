/* Copyright 2009-2012 David Hadka
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
 * The node for calculating the sign of a number.  The sign of a number is
 * {@code 1} if the number is greater than zero, {@code -1} if the number is
 * less than zero, and {@code 0} if the number is equal to zero.
 * 
 * @see Long#signum(long)
 * @see Math#signum(double)
 */
public class Sign extends Node {
	
	/**
	 * Constructs a new node for calculating the sign of a number.
	 */
	public Sign() {
		super(Number.class, Number.class);
	}
	
	@Override
	public Sign copyNode() {
		return new Sign();
	}
	
	@Override
	public Number evaluate(Environment environment) {
		return NumberArithmetic.sign(
				(Number)getArgument(0).evaluate(environment));
	}

}
