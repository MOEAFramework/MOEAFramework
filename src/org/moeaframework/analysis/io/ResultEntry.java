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
package org.moeaframework.analysis.io;

import org.moeaframework.core.TypedProperties;
import org.moeaframework.core.population.Population;

/**
 * An entry in a result file. This captures the population along with properties associated with the entry.  The
 * properties could include, for example, the number of function evaluations or performance metrics.
 * 
 * @see ResultFileWriter
 * @see ResultFileReader
 */
public class ResultEntry {
	
	/**
	 * The population stored in this entry.
	 */
	private final Population population;
	
	/**
	 * The auxiliary properties stored in this entry.
	 */
	private final TypedProperties properties;
	
	/**
	 * Constructs a result file entry with the specified population.
	 * 
	 * @param population the population stored in this entry
	 */
	public ResultEntry(Population population) {
		this(population, new TypedProperties());
	}

	/**
	 * Constructs a result file entry with the specified population and properties.
	 * 
	 * @param population the population stored in this entry
	 * @param properties the properties stored in this entry
	 */
	public ResultEntry(Population population, TypedProperties properties) {
		super();
		this.population = population;
		this.properties = properties;
	}

	/**
	 * Returns the population stored in this entry.
	 * 
	 * @return the population stored in this entry
	 */
	public Population getPopulation() {
		return population;
	}

	/**
	 * Returns the properties stored in this entry.
	 * 
	 * @return the properties stored in this entry
	 */
	public TypedProperties getProperties() {
		return properties;
	}

}
