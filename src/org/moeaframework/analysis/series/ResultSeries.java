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

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.moeaframework.analysis.io.ResultFileReader;
import org.moeaframework.analysis.stream.Streamable;
import org.moeaframework.core.population.Population;
import org.moeaframework.util.Iterators;
import org.moeaframework.util.format.Column;
import org.moeaframework.util.format.Formattable;
import org.moeaframework.util.format.TabularData;
import org.moeaframework.util.validate.Validate;

/**
 * Stores a collection {@link ResultEntry} in a series ordered by the index.  The specification of the index along with
 * the behavior depends on the selected {@link IndexType}.
 */
public class ResultSeries implements Serializable, Iterable<IndexedResult>, Formattable<IndexedResult>,
Streamable<IndexedResult> {
	
	private static final long serialVersionUID = -2606447194387896979L;
	
	/**
	 * The index type of this series.
	 */
	private final IndexType indexType;
	
	/**
	 * The result entries comprising this series.
	 */
	private final SortedMap<Integer, ResultEntry> data;
	
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
	 * Adds a new result entry to this series.
	 * 
	 * @param result the result to add
	 * @throws IllegalArgumentException if the entry is not compatible with the selected index type
	 */
	public void add(ResultEntry result) {
		switch (indexType) {
			case NFE -> {
				int index = result.getProperties().getInt(ResultEntry.NFE, -1);
				
				if (index < 0) {
					throw new IllegalArgumentException("Entry must define property '" + ResultEntry.NFE +
							" to be added to series with index " + IndexType.NFE.name());
				}
				
				if (data.containsKey(index)) {
					throw new IllegalArgumentException("Entry with index " + index + " already exists in series");
				}
				
				data.put(index, result);
			}
			case Index -> {
				data.put(data.size(), result);
			}
			case Singleton -> {
				if (data.size() > 0) {
					throw new IllegalArgumentException("Only one entry can be added to a " +
							IndexType.Singleton.name() + " series");
				}
				
				data.put(0, result);
			}
			default -> Validate.that("indexType", indexType).failUnsupportedOption();
		}
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
		return new IndexedResult(this, key, data.get(key));
	}
	
	/**
	 * Returns the last result in this series.
	 * 
	 * @return the last result
	 * @throws NoSuchElementException if the series is empty
	 */
	public IndexedResult last() {
		Integer key = data.lastKey();
		return new IndexedResult(this, key, data.get(key));
	}
	
	/**
	 * Returns the result at the specified index.  The behavior of this method depends on the {@link IndexType}.
	 * 
	 * @param index the index
	 * @return the result at the specified index
	 * @throws NoSuchElementException if the series is empty or no such entry matches the given index
	 */
	public IndexedResult at(int index) {
		return switch (indexType) {
			case NFE -> {
				Integer key = data.tailMap(index).firstKey();
				yield new IndexedResult(this, key, data.get(key));
			}
			case Index -> {
				ResultEntry result = data.get(index);
				
				if (result == null) {
					throw new NoSuchElementException();
				} else {
					yield new IndexedResult(this, index, result);
				}
			}
			case Singleton -> {
				Integer key = data.firstKey();
				yield new IndexedResult(this, index, data.get(key));
			}
		};
	}
	
	/**
	 * Returns {@code true} if there exists a result immediately following the current entry.
	 * 
	 * @param current the current entry
	 * @return {@code true} if there exists a result immediately following the current entry
	 */
	public boolean hasNext(IndexedResult current) {
		try {
			next(current);
			return true;
		} catch (NoSuchElementException e) {
			return false;
		}
	}
	
	/**
	 * Returns the result immediately following the current entry.
	 * 
	 * @param current the current entry
	 * @return the next entry
	 * @throws NoSuchElementException if there are no more results following the current entry
	 */
	public IndexedResult next(IndexedResult current) {
		Integer key = data.tailMap(current.getIndex() + 1).firstKey();
		return new IndexedResult(this, key, data.get(key));
	}
	
	/**
	 * Returns {@code true} if there exists a result immediately before the current entry.
	 * 
	 * @param current the current entry
	 * @return {@code true} if there exists a result immediately before the current entry; {@code false} otherwise
	 */
	public boolean hasPrevious(IndexedResult current) {
		try {
			previous(current);
			return true;
		} catch (NoSuchElementException e) {
			return false;
		}
	}
	
	/**
	 * Returns the result immediately before the current entry.
	 * 
	 * @param current the current entry
	 * @return the previous entry
	 * @throws NoSuchElementException if there are no more results before the current entry
	 */
	public IndexedResult previous(IndexedResult current) {
		Integer key = data.headMap(current.getIndex()).lastKey();
		return new IndexedResult(this, key, data.get(key));
	}
		
	/**
	 * Returns the starting or minimum index of this series.  Calls to {@link #at(int)} will succeed if the provided
	 * index is within the starting and ending index.
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
	 * Returns the ending or maximum index of this series.  Calls to {@link #at(int)} will succeed if the provided
	 * index is within the starting and ending index.
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
		return Iterators.map(data.entrySet().iterator(), x -> new IndexedResult(this, x.getKey(), x.getValue()));
	}
	
	@Override
	public TabularData<IndexedResult> asTabularData() {
		TabularData<IndexedResult> data = new TabularData<>(this);
		
		if (!isEmpty()) {
			if (!indexType.equals(IndexType.Singleton)) {
				data.addColumn(new Column<>(getIndexType().name(), IndexedResult::getIndex));
			}
			
			for (String key : getDefinedProperties()) {
				if (getIndexType().equals(IndexType.NFE) && key.equals(ResultEntry.NFE)) {
					continue;
				}
				
				data.addColumn(new Column<>(key, o -> o.getProperties().getString(key)));
			}
		}
		
		return data;
	}
	
	@Override
	public Stream<IndexedResult> stream() {
		return StreamSupport.stream(spliterator(), false);
	}
	
	/**
	 * Creates a series containing the given population, typically used to create a reference set.
	 * 
	 * @param population the population
	 * @return the series
	 */
	public static ResultSeries of(Population population) {
		ResultSeries series = new ResultSeries(IndexType.Singleton);
		series.add(new ResultEntry(population));
		return series;
	}
	
	/**
	 * Creates a series containing the populations contained in a result file.  If the first entry contains the
	 * property {@value ResultEntry#NFE}, then the index type {@link IndexType#NFE} is assumed and all remaining
	 * entries are expected to include the {@value ResultEntry#NFE} property.
	 * 
	 * @param reader the result file reader
	 * @return the series
	 */
	public static ResultSeries of(ResultFileReader reader) {
		ResultSeries series = null;
		
		for (ResultEntry result : reader) {
			if (series == null) {
				series = new ResultSeries(result.getProperties().contains(ResultEntry.NFE) ?
						IndexType.NFE : IndexType.Index);
			}

			series.add(result);
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