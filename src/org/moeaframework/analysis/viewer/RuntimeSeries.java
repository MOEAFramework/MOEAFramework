package org.moeaframework.analysis.viewer;

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

class RuntimeSeries implements Iterable<Pair<Integer, Population>> {
	
	private final String name;
	
	private final SortedMap<Integer, Population> data;
	
	RuntimeSeries(String name) {
		super();
		this.name = name;
		this.data = new TreeMap<Integer, Population>();
	}
	
	public String getName() {
		return name;
	}
	
	public void add(int nfe, Population set) {
		data.put(nfe, set);
	}
	
	public int size() {
		return data.size();
	}

	public boolean isEmpty() {
		return data.isEmpty();
	}

	public Pair<Integer, Population> first() {
		return Pair.of(data.firstEntry());
	}
	
	public Pair<Integer, Population> last() {
		return Pair.of(data.lastEntry());
	}
	
	public Pair<Integer, Population> at(int NFE) {
		try {
			return Pair.of(data.tailMap(NFE).firstEntry());
		} catch (NoSuchElementException e) {
			return null;
		}
	}
	
	public Pair<Range, Range> bounds(int NFE, Function<Solution, ? extends Number> getX,
			Function<Solution, ? extends Number> getY) {
		double domainMin = Double.POSITIVE_INFINITY;
		double domainMax = Double.NEGATIVE_INFINITY;
		double rangeMin = Double.POSITIVE_INFINITY;
		double rangeMax = Double.NEGATIVE_INFINITY;
		
		for (Solution solution : at(NFE).getValue()) {
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
	
	public XYSeries toXYSeries(int NFE, Function<Solution, ? extends Number> getX,
			Function<Solution, ? extends Number> getY) {
		return toXYSeries(name, at(NFE).getValue(), getX, getY);
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
	
	public static RuntimeSeries of(String name, Observations observations) {
		RuntimeSeries series = new RuntimeSeries(name);
		
		for (Observation observation : observations) {
			series.add(observation.getNFE(), ApproximationSetCollector.getApproximationSet(observation));
		}
		
		return series;
	}
	
	public static RuntimeSeries of(String name, ResultFileReader reader) {
		RuntimeSeries series = new RuntimeSeries(name);
		
		for (ResultEntry entry : reader) {
			series.add(entry.getProperties().getInt("NFE"), entry.getPopulation());
		}
		
		return series;
	}
	
}