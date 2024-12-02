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

import org.moeaframework.core.TypedProperties;
import org.moeaframework.core.population.Population;

/**
 * A {@link ResultEntry} with a defined index.
 */
public class IndexedResult extends ResultEntry implements Comparable<IndexedResult> {
	
	private static final long serialVersionUID = 7752235488283928332L;

	private final IndexType indexType;
	
	private final int index;
	
	/**
	 * Constructs an indexed result with the specified population.
	 * 
	 * @param indexType the index type
	 * @param index the index value
	 * @param population the population
	 */
	public IndexedResult(IndexType indexType, int index, Population population) {
		super(population, new TypedProperties());
		this.indexType = indexType;
		this.index = index;
	}

	/**
	 * Constructs an indexed result with the specified population and properties.
	 * 
	 * @param indexType the index type
	 * @param index the index value
	 * @param population the population
	 * @param properties the properties
	 */
	public IndexedResult(IndexType indexType, int index, Population population, TypedProperties properties) {
		super(population, properties);
		this.indexType = indexType;
		this.index = index;
	}
	
	/**
	 * Returns the index type for this result.
	 * 
	 * @return the index type
	 */
	public IndexType getIndexType() {
		return indexType;
	}

	/**
	 * Returns the index value for this result.  The interpretation of this value depends on the index type.
	 * 
	 * @return the index value
	 */
	public int getIndex() {
		return index;
	}

	@Override
	public int compareTo(IndexedResult other) {
		if (!getIndexType().equals(other.getIndexType())) {
			throw new IllegalArgumentException("Unable to compare results with different index types");
		}
		
		return Integer.compare(getIndex(), other.getIndex());
	}

}
