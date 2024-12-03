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
package org.moeaframework.analysis.series;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;

import org.moeaframework.core.TypedProperties;
import org.moeaframework.core.population.Population;

/**
 * Stores a population, typically the non-dominated result from an algorithm, along with any associated properties.
 */
public class ResultEntry implements Serializable {
	
	private static final long serialVersionUID = 8348128285443097003L;

	/**
	 * Property used to store the number of function evaluations (NFE).
	 */
	public static final String NFE = "NFE";
	
	/**
	 * Property used to store the elapsed wall-clock time.
	 */
	public static final String ElapsedTime = "ElapsedTime";
	
	/**
	 * The population associated with this result.
	 */
	private transient Population population;
	
	/**
	 * The properties associated with this result.
	 */
	private transient TypedProperties properties;
	
	/**
	 * Constructs a result with the specified population.
	 * 
	 * @param population the population
	 */
	public ResultEntry(Population population) {
		this(population, new TypedProperties());
	}

	/**
	 * Constructs a result with the specified population and properties.
	 * 
	 * @param population the population
	 * @param properties the properties
	 */
	public ResultEntry(Population population, TypedProperties properties) {
		super();
		this.population = population;
		this.properties = properties;
	}

	/**
	 * Returns the population associated with this result.
	 * 
	 * @return the population
	 */
	public Population getPopulation() {
		return population;
	}

	/**
	 * Returns the properties associated with this result.
	 * 
	 * @return the properties
	 */
	public TypedProperties getProperties() {
		return properties;
	}
	
	private void writeObject(ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
		
		population.saveState(out);
		
		try (StringWriter writer = new StringWriter()) {
			properties.save(writer);
			out.writeObject(writer.toString());
		}
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
		in.defaultReadObject();
		
		population = new Population();
		population.loadState(in);
		
		try (StringReader reader = new StringReader((String)in.readObject())) {
			properties = new TypedProperties();
			properties.load(reader);
		}
	}

}
