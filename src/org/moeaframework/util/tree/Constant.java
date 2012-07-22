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

public class Constant extends Node {
	
	private final Object value;
	
	public Constant(double value) {
		this(Number.class, value);
	}
	
	public Constant(long value) {
		this(Number.class, value);
	}
	
	public Constant(boolean value) {
		this(Boolean.class, value);
	}
	
	public Constant(Class<?> returnType, Object value) {
		super(returnType);
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
	
	public String toString() {
		return String.valueOf(value);
	}

}
