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
 * The node for defining a constant value.
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
		this((Double)value);
	}
	
	/**
	 * Constructs a new node for defining a constant integer value.
	 * 
	 * @param value the integer value
	 */
	public Constant(long value) {
		this((Long)value);
	}
	
	/**
	 * Constructs a new node for defining a constant boolean value.
	 * 
	 * @param value the boolean value
	 */
	public Constant(boolean value) {
		this((Boolean)value);
	}
	
	/**
	 * Constructs a new node for defining a constant {@link Object}.
	 * 
	 * @param value the object
	 */
	public Constant(Object value) {
		super(value.getClass());
		this.value = value;
	}
	
	@Override
	public Constant copyNode() {
		return new Constant(value);
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
