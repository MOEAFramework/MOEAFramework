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

import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.util.TypedProperties;

/**
 * An entry in a result file. This captures the non-dominated population along with properties associated with the
 * entry. The properties could include, for example, the number of function evaluations or performance metrics.
 * 
 * @see ResultFileWriter
 * @see ResultFileReader
 */
public class ResultEntry {
	
	/**
	 * The non-dominated population stored in this entry.
	 */
	private final NondominatedPopulation population;
	
	/**
	 * The auxiliary properties stored in this entry.
	 */
	private final TypedProperties properties;
	
	/**
	 * Constructs a result file entry with the specified non-dominated population.
	 * 
	 * @param population the non-dominated population stored in this entry
	 */
	public ResultEntry(NondominatedPopulation population) {
		this(population, new TypedProperties());
	}

	/**
	 * Constructs a result file entry with the specified non-dominated population and auxiliary properties.
	 * 
	 * @param population the non-dominated population stored in this entry
	 * @param properties the auxiliary properties stored in this entry
	 */
	public ResultEntry(NondominatedPopulation population, TypedProperties properties) {
		super();
		this.population = population;
		this.properties = properties;
	}

	/**
	 * Returns the non-dominated population stored in this entry.
	 * 
	 * @return the non-dominated population stored in this entry
	 */
	public NondominatedPopulation getPopulation() {
		return population;
	}

	/**
	 * Returns the auxiliary properties stored in this entry.
	 * 
	 * @return the auxiliary properties stored in this entry
	 */
	public TypedProperties getProperties() {
		return properties;
	}

}
