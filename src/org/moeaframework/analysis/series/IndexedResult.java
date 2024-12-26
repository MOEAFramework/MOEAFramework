/* Copyright 2009-2025 David Hadka
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

import java.util.NoSuchElementException;

import org.moeaframework.core.TypedProperties;
import org.moeaframework.core.population.Population;

/**
 * References a {@link ResultEntry} stored in a series, in particular providing access to the index along with methods
 * to iterate through the series.
 */
public class IndexedResult {
	
	private final ResultSeries series;
	
	private final int index;
	
	private final ResultEntry entry;
	
	/**
	 * Constructs a new indexed result.
	 * 
	 * @param series the series containing this result
	 * @param index the index of this result in the series
	 * @param entry the underlying result
	 */
	public IndexedResult(ResultSeries series, int index, ResultEntry entry) {
		super();
		this.index = index;
		this.series = series;
		this.entry = entry;
	}

	/**
	 * Returns the series containing this result.
	 * 
	 * @return the series containing this result
	 */
	public ResultSeries getSeries() {
		return series;
	}

	/**
	 * Returns the index of this result in the series.
	 * 
	 * @return the index of this result
	 */
	public int getIndex() {
		return index;
	}
	
	/**
	 * Returns the underlying result entry.
	 * 
	 * @return the result entry
	 */
	public ResultEntry getEntry() {
		return entry;
	}

	/**
	 * Returns the population contained in this result.  This is shorthand for calling {@link #getEntry()}.
	 * 
	 * @return the population
	 */
	public Population getPopulation() {
		return entry.getPopulation();
	}
	
	/**
	 * Returns the properties contained in this result.  This is shorthand for calling {@link #getEntry()}.
	 * 
	 * @return the properties
	 */
	public TypedProperties getProperties() {
		return entry.getProperties();
	}
	
	/**
	 * Returns {@code true} if there exists a result following this entry in the series such that calling
	 * {@link #next()} would succeed.
	 * 
	 * @return {@code true} if there exists a result following this entry in the series; {@code false} otherwise
	 */
	public boolean hasNext() {
		return series.hasNext(this);
	}
	
	/**
	 * Returns the result immediately following this entry in the series.
	 * 
	 * @return the next result
	 * @throws NoSuchElementException if at the end of the series
	 */
	public IndexedResult next() {
		return series.next(this);
	}
	
	/**
	 * Returns {@code true} if there exists a result before this entry in the series such that calling
	 * {@link #previous()} would succeed.
	 * 
	 * @return {@code true} if there exists a result before this entry in the series; {@code false} otherwise
	 */
	public boolean hasPrevious() {
		return series.hasPrevious(this);
	}
	
	/**
	 * Returns the result immediately before this entry in the series.
	 * 
	 * @return the previous result
	 * @throws NoSuchElementException if at the start of the series
	 */
	public IndexedResult previous() {
		return series.previous(this);
	}

}
