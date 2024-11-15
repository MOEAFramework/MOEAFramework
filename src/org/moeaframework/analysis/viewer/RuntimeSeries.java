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

public class RuntimeSeries implements Iterable<Pair<Integer, Population>> {
	
	public static enum IndexType {
		
		NFE,
		
		Index,
		
		Singleton
		
	}
	
	private final String name;
		
	private final SortedMap<Integer, Population> data;
	
	private final IndexType indexType;
	
	RuntimeSeries(String name, IndexType indexType) {
		super();
		this.name = name;
		this.indexType = indexType;
		this.data = new TreeMap<Integer, Population>();
	}
	
	public String getName() {
		return name;
	}
	
	public IndexType getIndexType() {
		return indexType;
	}
	
	public void add(int index, Population set) {
		data.put(index, set);
	}
	
	public int size() {
		return data.size();
	}

	public boolean isEmpty() {
		return data.isEmpty();
	}

	public Pair<Integer, Population> first() {
		Integer key = data.firstKey();
		return Pair.of(key, data.get(key));
	}
	
	public Pair<Integer, Population> last() {
		Integer key = data.lastKey();
		return Pair.of(key, data.get(key));
	}
	
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
	
	public int getStartingIndex() {
		return switch(indexType) {
			case NFE -> 0;
			case Index, Singleton -> first().getKey();
		};
	}
	
	public int getEndingIndex() {
		return last().getKey();
	}
	
	public int getStepSize() {
		return switch(indexType) {
			case NFE -> (getEndingIndex() - getStartingIndex()) / size();
			case Index, Singleton -> 1;
		};
	}
	
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
	
	public XYSeries toXYSeries(int index, Function<Solution, ? extends Number> getX,
			Function<Solution, ? extends Number> getY) {
		Pair<Integer, Population> entry = at(index);
		
		if (entry == null || entry.getValue() == null) {
			return new XYSeries(name, false, true);
		}
		
		return toXYSeries(name, entry.getValue(), getX, getY);
	}

	@Override
	public Iterator<Pair<Integer, Population>> iterator() {
		return Iterators.map(data.entrySet().iterator(), e -> Pair.of(e));
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public static XYSeries toXYSeries(String title, Population population, Function<Solution, ? extends Number> getX,
			Function<Solution, ? extends Number> getY) {
		XYSeries series = new XYSeries(title, false, true);
		
		for (Solution solution : population) {
			series.add(getX.apply(solution), getY.apply(solution));
		}
		
		return series;
	}
	
	public static RuntimeSeries of(String name, Population population) {
		RuntimeSeries series = new RuntimeSeries(name, IndexType.Singleton);
		series.add(0, population);
		return series;
	}
	
	public static RuntimeSeries of(String name, Observations observations) {
		RuntimeSeries series = new RuntimeSeries(name, IndexType.NFE);
		
		for (Observation observation : observations) {
			series.add(observation.getNFE(), ApproximationSetCollector.getApproximationSet(observation));
		}
		
		return series;
	}
	
	public static RuntimeSeries of(String name, ResultFileReader reader) {
		RuntimeSeries series = null;
		
		for (ResultEntry entry : reader) {
			if (series == null) {
				series = new RuntimeSeries(name, entry.getProperties().contains("NFE") ?
						IndexType.NFE : IndexType.Index);
			}

			int index = switch (series.getIndexType()) {
				case NFE -> entry.getProperties().getInt("NFE");
				case Index -> series.size();
				case Singleton -> 0;
			};
			
			series.add(index, entry.getPopulation());
		}
		
		return series;
	}
	
	public static RuntimeSeries of(File file) throws IOException {
		try (ResultFileReader reader = ResultFileReader.openLegacy(null, file)) {
			return of(file.getName(), reader);
		}
	}
	
}