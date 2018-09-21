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

import java.util.HashMap;
import java.util.Map;

/**
 * The execution context, or environment, storing the named variables used by
 * a program.  Defined functions are first-class objects and also stored in the
 * environment.
 */
public class Environment {
	
	/**
	 * The enclosing environment.
	 */
	private Environment parent;
	
	/**
	 * The mapping of names to stored variables and functions.
	 */
	private Map<String, Object> memory;
	
	/**
	 * Constructs a new, empty environment.
	 */
	public Environment() {
		this(null);
	}
	
	/**
	 * Constructs a new environment enclosed within the specified enclosing
	 * environment.
	 * 
	 * @param parent the enclosing environment
	 */
	public Environment(Environment parent) {
		super();
		this.parent = parent;
		
		memory = new HashMap<String, Object>();
	}
	
	/**
	 * Returns the content of the named variable stored in this environment or
	 * its enclosing environment; or {@code null} if the variable has not yet
	 * been defined.
	 * 
	 * @param type the type of the variable
	 * @param name the name of the variable
	 * @return the content of the named variable stored in this environment or
	 *         its enclosing environment; or {@code null} if the variable has
	 *         not yet been defined
	 */
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
	
	/**
	 * Sets the content of the named variable to the specified value.
	 * 
	 * @param name the name of the variable
	 * @param value the value to store in the variable
	 */
	public void set(String name, Object value) {
		memory.put(name, value);
	}

}
