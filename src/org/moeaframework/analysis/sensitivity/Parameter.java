/* Copyright 2009-2016 David Hadka
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
package org.moeaframework.analysis.sensitivity;

/**
 * Defines a parameter.
 */
public class Parameter {

	/**
	 * The name of this parameter.
	 */
	private final String name;

	/**
	 * The lower bound of this parameter.
	 */
	private final double lowerBound;

	/**
	 * The upper bound of this parameter.
	 */
	private final double upperBound;

	/**
	 * Constructs a new parameter with the specified name, lower bound, and 
	 * upper bound.
	 * 
	 * @param name the parameter name
	 * @param lowerBound the lower bound of the parameter
	 * @param upperBound the upper bound of the parameter
	 */
	public Parameter(String name, double lowerBound, double upperBound) {
		super();
		this.name = name;
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}

	/**
	 * Returns the name of this parameter.
	 * 
	 * @return the name of this parameter.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the lower bound of this parameter.
	 * 
	 * @return the lower bound of this parameter
	 */
	public double getLowerBound() {
		return lowerBound;
	}

	/**
	 * Returns the upper bound of this parameter.
	 * 
	 * @return the upper bound of this parameter
	 */
	public double getUpperBound() {
		return upperBound;
	}

}
