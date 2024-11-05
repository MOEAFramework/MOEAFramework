/* Copyright 2009-2024 David Hadka
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
package org.moeaframework.analysis.parameter;

/**
 * Abstract parameter, primarily for storing the name.
 * 
 * @param <T> the type of the parameter
 */
public abstract class AbstractParameter<T> implements Parameter<T> {
	
	private final String name;
	
	/**
	 * Constructs a new parameter with the given name.
	 * 
	 * @param name the parameter name
	 */
	public AbstractParameter(String name) {
		super();
		this.name = name;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
}
