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

import java.util.HashMap;
import java.util.Map;

public class Environment {
	
	private Environment parent;
	
	private Map<String, Object> memory;
	
	public Environment() {
		this(null);
	}
	
	public Environment(Environment parent) {
		super();
		this.parent = parent;
		
		memory = new HashMap<String, Object>();
	}
	
	public <T> T get(Class<T> type, String name) {
		Object value = memory.get(name);
		
		if ((value == null) && (parent != null)) {
			value = parent.get(type, name);
		}
		
		if (value == null) {
			return null;
		} else {
			return type.cast(value);
		}
	}
	
	public void set(String name, Object value) {
		memory.put(name, value);
	}

}
