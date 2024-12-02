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

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import org.moeaframework.analysis.io.ResultFileReader;
import org.moeaframework.core.population.Population;
import org.moeaframework.util.format.Column;
import org.moeaframework.util.format.Formattable;
import org.moeaframework.util.format.TabularData;

/**
 * A series of {@link IndexedResult}.  All results must use the same index type as this series, which also determines
 * how the indices are interpreted.
 */
public class ResultSeries implements Serializable, Iterable<IndexedResult>, Formattable<IndexedResult> {
	
	private static final long serialVersionUID = -2606447194387896979L;
	
	private final IndexType indexType;
	
	private final SortedMap<Integer, IndexedResult> data;
	
	/**
	 * Constructs a new, empty series.
	 * 
	 * @param indexType the index type
	 */
	public ResultSeries(IndexType indexType) {
		super();
		this.indexType = indexType;
		this.data = new TreeMap<>();
	}

	/**
	 * Returns the index type for this series.
	 * 
	 * @return the index type
	 */
	public IndexType getIndexType() {
		return indexType;
	}
	
	/**
	 * Adds a new indexed result.
	 * 
	 * @param result the indexed result
	 */
	public void add(IndexedResult result) {
		if (!result.getIndexType().equals(indexType)) {
			throw new IllegalArgumentException("unable to add result with index type " + result.getIndexType() +
					" to series with index type " + indexType);
		}
		
		data.put(result.getIndex(), result);
	}
	
	/**
	 * Returns the size of this series.
	 * 
	 * @return the size of this series
	 */
	public int size() {
		return data.size();
	}

	/**
	 * Returns {@code true} if this series is empty; {@code false} otherwise.
	 * 
	 * @return {@code true} if this series is empty; {@code false} otherwise
	 */
	public boolean isEmpty() {
		return data.isEmpty();
	}

	/**
	 * Returns the first result in this series.
	 * 
	 * @return the first result
	 * @throws NoSuchElementException if the series is empty
	 */
	public IndexedResult first() {
		Integer key = data.firstKey();
		
		if (key == null) {
			throw new NoSuchElementException();
		} else {
			return data.get(key);
		}
	}
	
	/**
	 * Returns the last result in this series.
	 * 
	 * @return the last result
	 * @throws NoSuchElementException if the series is empty
	 */
	public IndexedResult last() {
		Integer key = data.lastKey();
		
		if (key == null) {
			throw new NoSuchElementException();
		} else {
			return data.get(key);
		}
	}
	
	/**
	 * Returns the result at the specified index.
	 * 
	 * @param index the index
	 * @return the result at the specified index
	 * @throws NoSuchElementException if the series is empty or no such entry matches the given index
	 */
	public IndexedResult at(int index) {
		return switch (indexType) {
			case NFE -> {
				Integer key = data.tailMap(index).firstKey();
				
				if (key == null) {
					throw new NoSuchElementException();
				} else {
					yield data.get(key);
				}
			}
			case Index -> {
				IndexedResult result = data.get(index);
				
				if (result == null) {
					throw new NoSuchElementException();
				} else {
					yield result;
				}
			}
			case Singleton -> {
				yield first();
			}
		};
	}
	
	/**
	 * Returns the result immediately following the specified index.
	 * 
	 * @param index the index
	 * @return the next result after the index
	 * @throws NoSuchElementException if there are no more results following the given index
	 */
	public IndexedResult next(int index) {
		return at(index + 1);
	}
		
	/**
	 * Returns the starting or minimum index.
	 * 
	 * @return the starting index
	 * @throws NoSuchElementException if the series is empty
	 */
	public int getStartingIndex() {
		return switch(indexType) {
			case NFE -> 0;
			case Index, Singleton -> first().getIndex();
		};
	}
	
	/**
	 * Returns the ending or maximum index.
	 * 
	 * @return the ending index
	 * @throws NoSuchElementException if the series is empty
	 */
	public int getEndingIndex() {
		return last().getIndex();
	}
	
	/**
	 * Returns the set of property keys that are defined in all results contained within this series.
	 * 
	 * @return the set of property keys
	 */
	public Set<String> getDefinedProperties() {
		Set<String> keys = null;
		
		for (IndexedResult result : this) {
			if (keys == null) {
				keys = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
				keys.addAll(result.getProperties().keySet());
			} else {
				keys.retainAll(result.getProperties().keySet());
			}
		}

		return keys != null ? keys : new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
	}

	@Override
	public Iterator<IndexedResult> iterator() {
		return data.values().iterator();
	}
	
	@Override
	public TabularData<IndexedResult> asTabularData() {
		TabularData<IndexedResult> data = new TabularData<IndexedResult>(this);
		
		if (!isEmpty()) {
			data.addColumn(new Column<IndexedResult, Integer>(getIndexType().name(), o -> o.getIndex()));
			
			for (String key : getDefinedProperties()) {
				if (getIndexType().equals(IndexType.NFE) && key.equals(ResultEntry.NFE)) {
					// skip NFE property if index already represents the NFE
					continue;
				}
				
				data.addColumn(new Column<IndexedResult, String>(key, o -> o.getProperties().getString(key)));
			}
		}
		
		return data;
	}
	
	/**
	 * Creates a series containing the given population, typically used to create a reference set.
	 * 
	 * @param population the population
	 * @return the series
	 */
	public static ResultSeries of(Population population) {
		ResultSeries series = new ResultSeries(IndexType.Singleton);
		series.add(new IndexedResult(IndexType.Singleton, 0, population));
		return series;
	}
	
	/**
	 * Creates a series containing the populations contained in a result file.  The result file should contain the
	 * property {@value ResultEntry#NFE} on each entry in order to use NFE as the ordering.
	 * 
	 * @param reader the result file reader
	 * @return the series
	 */
	public static ResultSeries of(ResultFileReader reader) {
		ResultSeries series = null;
		
		for (ResultEntry result : reader) {			
			IndexType indexType = result.getProperties().contains(ResultEntry.NFE) ? IndexType.NFE : IndexType.Index;
			
			if (series == null) {
				series = new ResultSeries(indexType);
			}

			int index = switch (indexType) {
				case NFE -> result.getProperties().getInt(ResultEntry.NFE);
				case Index -> series.size();
				case Singleton -> 0;
			};
			
			series.add(new IndexedResult(series.getIndexType(), index, result.getPopulation(), result.getProperties()));
		}
		
		return series;
	}
	
	/**
	 * Creates a series containing the populations contained in a result file.
	 * 
	 * @param file the result file
	 * @return the series
	 * @see #of(ResultFileReader)
	 * @throws IOException if an I/O error occurred
	 */
	public static ResultSeries of(File file) throws IOException {
		try (ResultFileReader reader = ResultFileReader.openLegacy(null, file)) {
			return of(reader);
		}
	}
	
}