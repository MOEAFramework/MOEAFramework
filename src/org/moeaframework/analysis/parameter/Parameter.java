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

import org.moeaframework.analysis.sample.Sample;
import org.moeaframework.core.Named;

/**
 * Represents a typed parameter.
 * 
 * @param <T> the type of the parameter
 * @see Sample
 */
public interface Parameter<T> extends Named {

	/**
	 * Parses this parameter from the given string.
	 * 
	 * @param str the string
	 * @throws InvalidParameterException if the given string is invalid
	 */
	public T parse(String str);
	
	/**
	 * Reads the parameter value from the given sample.
	 * 
	 * @param sample the sample
	 * @return the parameter value
	 */
	public default T getValue(Sample sample) {
		return parse(sample.getString(getName()));
	}
	
	/**
	 * Entry point to using the parameter builder.  This starts by specifying the name, with additional options being
	 * configured on the returned builder.
	 * 
	 * @param name the parameter name
	 * @return the parameter builder
	 */
	public static ParameterBuilder named(String name) {
		return new ParameterBuilder(name);
	}
	
}
