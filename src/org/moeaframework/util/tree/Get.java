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

public class Get extends Node {
	
	private final String name;
	
	public Get(Class<?> type, String name) {
		super(type);
		this.name = name;
	}

	@Override
	public Get copyNode() {
		return new Get(getReturnType(), name);
	}

	@Override
	public Object evaluate(Environment environment) {
		Object value = environment.get(getReturnType(), name);
		
		if (value == null) {
			if (getReturnType().equals(Float.class) || 
					getReturnType().equals(Double.class)) {
				value = 0.0;
			} else {
				value = 0;
			}
		}
		
		return value;
	}
	
	public Object getDefaultValue() {
		if (getReturnType().equals(Byte.class) ||
				getReturnType().equals(Short.class) ||
				getReturnType().equals(Integer.class) || 
				getReturnType().equals(Long.class)) {
			return 0;
		} else if (getReturnType().equals(Float.class) ||
				getReturnType().equals(Double.class)) {
			return 0.0;
		} else if (getReturnType().equals(Boolean.class)) {
			return false;
		} else {
			return null;
		}
	}
	
	public String toString() {
		return name;
	}

}
