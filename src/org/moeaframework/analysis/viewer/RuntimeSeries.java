package org.moeaframework.analysis.viewer;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;
import org.jfree.data.Range;
import org.jfree.data.xy.XYSeries;
import org.moeaframework.analysis.io.ResultEntry;
import org.moeaframework.analysis.io.ResultFileReader;
import org.moeaframework.analysis.runtime.ApproximationSetCollector;
import org.moeaframework.analysis.runtime.Observation;
import org.moeaframework.analysis.runtime.Observations;
import org.moeaframework.core.Solution;
import org.moeaframework.core.population.Population;
import org.moeaframework.util.Iterators;

/**
 * A series of populations, typically representing the runtime approximation sets.
 */
public class RuntimeSeries implements Iterable<Pair<Integer, Population>> {
	
	/**
	 * Determines the type of index / key used by this series.
	 */
	public static enum IndexType {
		
		/**
		 * The key represents the number of function evaluations (NFE).  When finding the entry with a given NFE,
		 * we typically find the nearest match.
		 */
		NFE,
		
		/**
		 * The key represents the index of the item in the result file.  This typically means the entries are not
		 * associated with a single run of an algorithm.
		 */
		Index,
		
		/**
		 * The series contains a single entry, which is returned regardless of what index is provided.  This is
		 * typically used by reference sets.
		 */
		Singleton
		
	}
	
	private final String name;
		
	private final SortedMap<Integer, Population> data;
	
	private final IndexType indexType;
	
	/**
	 * Constructs a new, empty series.
	 * 
	 * @param name the series name
	 * @param indexType the index / key type
	 */
	RuntimeSeries(String name, IndexType indexType) {
		super();
		this.name = name;
		this.indexType = indexType;
		this.data = new TreeMap<Integer, Population>();
	}
	
	/**
	 * Returns the name of this series.
	 * 
	 * @return the series name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the index / key type for this series.
	 * 
	 * @return the index / key type
	 */
	public IndexType getIndexType() {
		return indexType;
	}
	
	/**
	 * Adds a new entry at the given index.
	 * 
	 * @param index the index
	 * @param set the population to add
	 */
	public void add(int index, Population set) {
		data.put(index, set);
	}
	
	/**
	 * Returns the number of entries in this series.
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
	 * Returns the first entry in this series.
	 * 
	 * @return the first entry in this series
	 */
	public Pair<Integer, Population> first() {
		Integer key = data.firstKey();
		return Pair.of(key, data.get(key));
	}
	
	/**
	 * Returns the last entry in this series.
	 * 
	 * @return the last entry in this series
	 */
	public Pair<Integer, Population> last() {
		Integer key = data.lastKey();
		return Pair.of(key, data.get(key));
	}
	
	/**
	 * Returns the entry at the specified index.  Note that the {@link IndexType} determines how indices are matched.
	 * 
	 * @param index the index
	 * @return the entry at the specified index, or {@code null} if no such entry exists
	 */
	public Pair<Integer, Population> at(int index) {
		try {
			return switch (indexType) {
				case NFE -> {
					Integer key = data.tailMap(index).firstKey();
					yield Pair.of(key, data.get(key));
				}
				case Index -> {
					yield index < data.size() ? Pair.of(index, data.get(index)) : null;
				}
				case Singleton -> {
					Integer key = data.firstKey();
					yield Pair.of(key, data.get(key));
				}
			};
		} catch (NoSuchElementException e) {
			return null;
		}
	}
	
	/**
	 * Returns the entry immediately following the specified index.
	 * 
	 * @param index the index
	 * @return the next entry after the index, or {@code null} if no such entry exists
	 */
	public Pair<Integer, Population> next(int index) {
		return at(index + 1);
	}
	
	/**
	 * Returns the starting or minimum index.
	 * 
	 * @return the starting index
	 */
	public int getStartingIndex() {
		return switch(indexType) {
			case NFE -> 0;
			case Index, Singleton -> first().getKey();
		};
	}
	
	/**
	 * Returns the ending or maximum index.
	 * 
	 * @return the ending index
	 */
	public int getEndingIndex() {
		return last().getKey();
	}
	
	/**
	 * Returns an estimate of the step size.  This should, in practice, result in each entry being visited when
	 * iterating between the starting and ending index, assuming the entries are evenly spaced.  If you need to visit
	 * every entry, use {@link #iterator()}.
	 * 
	 * @return the step size
	 */
	public int getStepSize() {
		return switch(indexType) {
			case NFE -> (getEndingIndex() - getStartingIndex()) / size();
			case Index, Singleton -> 1;
		};
	}
	
	/**
	 * Returns a pair of ranges, for the X and Y axes, representing the minimum and maximum values.
	 * 
	 * @param index the index
	 * @param getX a function to read the X value
	 * @param getY a function to read the Y value
	 * @return the X and Y ranges, or {@code null} if the entry does not exist
	 */
	public Pair<Range, Range> bounds(int index, Function<Solution, ? extends Number> getX,
			Function<Solution, ? extends Number> getY) {
		Pair<Integer, Population> entry = at(index);
		
		if (entry == null || entry.getValue() == null) {
			return null;
		}
		
		double domainMin = Double.POSITIVE_INFINITY;
		double domainMax = Double.NEGATIVE_INFINITY;
		double rangeMin = Double.POSITIVE_INFINITY;
		double rangeMax = Double.NEGATIVE_INFINITY;
		
		for (Solution solution : entry.getValue()) {
			double xValue = getX.apply(solution).doubleValue();
			double yValue = getY.apply(solution).doubleValue();
				
			domainMin = Math.min(domainMin, xValue);
			domainMax = Math.max(domainMax, xValue);
			rangeMin = Math.min(rangeMin, yValue);
			rangeMax = Math.max(rangeMax, yValue);
		}
		
		double domainDelta = 0.1 * (domainMax - domainMin);
		double rangeDelta = 0.1 * (rangeMax - rangeMin);

		return Pair.of(
				new Range(domainMin - domainDelta, domainMax + domainDelta),
				new Range(rangeMin - rangeDelta, rangeMax + rangeDelta));
	}
	
	/**
	 * Converts the entry at the specified index to an XY series.  If the entry does not exist, an empty XY series
	 * is returned.
	 * 
	 * @param index the index
	 * @param getX a function to read the X value
	 * @param getY a function to read the Y value
	 * @return the XY series
	 */
	public XYSeries toXYSeries(int index, Function<Solution, ? extends Number> getX,
			Function<Solution, ? extends Number> getY) {
		Pair<Integer, Population> entry = at(index);
		
		if (entry == null || entry.getValue() == null) {
			return new XYSeries(name, false, true);
		}
		
		XYSeries series = new XYSeries(name, false, true);
		
		for (Solution solution : entry.getValue()) {
			series.add(getX.apply(solution), getY.apply(solution));
		}
		
		return series;
	}

	@Override
	public Iterator<Pair<Integer, Population>> iterator() {
		return Iterators.map(data.entrySet().iterator(), e -> Pair.of(e));
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	/**
	 * Creates a series containing the given population, typically used to create a reference set.
	 * 
	 * @param name the name for the series
	 * @param population the population
	 * @return the series
	 */
	public static RuntimeSeries of(String name, Population population) {
		RuntimeSeries series = new RuntimeSeries(name, IndexType.Singleton);
		series.add(0, population);
		return series;
	}
	
	/**
	 * Creates a series containing the approximation sets for the given observations.
	 * 
	 * @param name the name for the series
	 * @param observations the observations
	 * @return the series
	 */
	public static RuntimeSeries of(String name, Observations observations) {
		RuntimeSeries series = new RuntimeSeries(name, IndexType.NFE);
		
		for (Observation observation : observations) {
			series.add(observation.getNFE(), ApproximationSetCollector.getApproximationSet(observation));
		}
		
		return series;
	}
	
	/**
	 * Creates a series containing the populations contained in a result file.  The result file should contain the
	 * property {@value ResultEntry#NFE} on each entry in order to use NFE as the ordering.
	 * 
	 * @param name the name for the series
	 * @param reader the result file reader
	 * @return the series
	 */
	public static RuntimeSeries of(String name, ResultFileReader reader) {
		RuntimeSeries series = null;
		
		for (ResultEntry entry : reader) {
			if (series == null) {
				series = new RuntimeSeries(name, entry.getProperties().contains(ResultEntry.NFE) ?
						IndexType.NFE : IndexType.Index);
			}

			int index = switch (series.getIndexType()) {
				case NFE -> entry.getProperties().getInt(ResultEntry.NFE);
				case Index -> series.size();
				case Singleton -> 0;
			};
			
			series.add(index, entry.getPopulation());
		}
		
		return series;
	}
	
	/**
	 * Creates a series containing the populations contained in a result file.
	 * 
	 * @param file the result file
	 * @return the series
	 * @see #of(String, ResultFileReader)
	 * @throws IOException if an I/O error occurred
	 */
	public static RuntimeSeries of(File file) throws IOException {
		try (ResultFileReader reader = ResultFileReader.openLegacy(null, file)) {
			return of(file.getName(), reader);
		}
	}
	
}