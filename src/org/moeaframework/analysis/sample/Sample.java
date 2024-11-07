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
package org.moeaframework.analysis.sample;

import org.moeaframework.analysis.parameter.Parameter;
import org.moeaframework.analysis.store.Reference;
import org.moeaframework.analysis.store.Referenceable;
import org.moeaframework.core.TypedProperties;

/**
 * A single parameter sample.
 * 
 * @see Parameter
 * @see Samples
 */
public class Sample extends TypedProperties implements Referenceable {

	/**
	 * Constructs a new, empty sample.
	 */
	public Sample() {
		super(TypedProperties.DEFAULT_SEPARATOR, true);
	}

	/**
	 * Creates a copy of this parameter.
	 * 
	 * @return the copy
	 */
	public Sample copy() {
		Sample copy = new Sample();
		copy.addAll(this);
		return copy;
	}
	
	/**
	 * Reads the value of a parameter from this sample.
	 * 
	 * @param <T> the type of the parameter
	 * @param parameter the parameter
	 * @return the parameter value
	 */
	public <T> T get(Parameter<T> parameter) {
		return parameter.readValue(this);
	}

	@Override
	public Reference getReference() {
		return Reference.of(this);
	}

}