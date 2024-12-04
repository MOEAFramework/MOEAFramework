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
package org.moeaframework.analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.UnivariateStatistic;
import org.moeaframework.core.indicator.Indicator;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.util.format.Column;
import org.moeaframework.util.format.Formattable;
import org.moeaframework.util.format.TabularData;
import org.moeaframework.util.statistics.KruskalWallisTest;
import org.moeaframework.util.statistics.MannWhitneyUTest;

/**
 * Utility for collecting end-of-run approximation sets, evaluating a performance indicator, and displaying statistical
 * results.
 */
public class IndicatorStatistics implements Formattable<String> {
	
	private final Indicator indicator;
	
	/**
	 * The collection of end-of-run approximation sets.
	 */
	private final Map<String, DescriptiveStatistics> data;
	
	/**
	 * Constructs a new indicator statistics object with the given indicator.
	 * 
	 * @param indicator the performance indicator
	 */
	public IndicatorStatistics(Indicator indicator) {
		super();
		this.indicator = indicator;
		this.data = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
	}
	
	/**
	 * Adds the given end-of-run approximation set.
	 * 
	 * @param name the group name
	 * @param result the end-of-run approximation set
	 */
	public void add(String name, NondominatedPopulation result) {
		DescriptiveStatistics stats = data.get(name);
		
		if (stats == null) {
			stats = new DescriptiveStatistics();
			data.put(name, stats);
		}
		
		stats.addValue(indicator.evaluate(result));
	}
	
	/**
	 * Adds the collection of end-of-run approximation sets.
	 * 
	 * @param name the group name
	 * @param results the collection of end-of-run approximation sets
	 */
	public void addAll(String name, Iterable<NondominatedPopulation> results) {
		for (NondominatedPopulation result : results) {
			add(name, result);
		}
	}
	
	/**
	 * Returns the group names.
	 * 
	 * @return the group names
	 */
	public Set<String> getGroupNames() {
		return Collections.unmodifiableSet(data.keySet());
	}
	
	/**
	 * Returns the number of results recorded for the given group.
	 * 
	 * @param name the group name
	 * @return the count
	 */
	public int getN(String name) {
		return (int)data.get(name).getN();
	}
	
	/**
	 * Returns the minimum indicator value for the given group.
	 * 
	 * @param name the group name
	 * @return the minimum value
	 */
	public double getMin(String name) {
		return data.get(name).getMin();
	}
	
	/**
	 * Returns the maximum indicator value for the given group.
	 * 
	 * @param name the group name
	 * @return the maximum value
	 */
	public double getMax(String name) {
		return data.get(name).getMax();
	}
	
	/**
	 * Returns the mean or average indicator value for the given group.
	 * 
	 * @param name the group name
	 * @return the mean value
	 */
	public double getMean(String name) {
		return data.get(name).getMean();
	}
	
	/**
	 * Returns the median indicator value for the given group.
	 * 
	 * @param name the group name
	 * @return the median value
	 */
	public double getMedian(String name) {
		return data.get(name).getPercentile(50);
	}
	
	/**
	 * Returns the inter-quartile range (IQR) indicator value for the given group, which is the range between the
	 * 75-th percentile and the 25-th percentile.
	 * 
	 * @param name the group name
	 * @return the IQR value
	 */
	public double getIQR(String name) {
		return data.get(name).getPercentile(75) - data.get(name).getPercentile(25);
	}
	
	/**
	 * Returns the value of the given custom statistic.
	 * 
	 * @param name the group name
	 * @param statistic the custom statistic
	 * @return the statistic value
	 */
	public double getStatistic(String name, UnivariateStatistic statistic) {
		return data.get(name).apply(statistic);
	}
	
	/**
	 * Returns the indicator values for the given group.
	 * 
	 * @param name the group name
	 * @return the indicator values
	 */
	public double[] getValues(String name) {
		return data.get(name).getValues();
	}
	
	/**
	 * Computes and returns the groups that are statistically similar to the given group.
	 * 
	 * @param name the given group
	 * @param significanceLevel the level of significance
	 * @return the other groups that are statistically similar
	 * @see #getStatisticallySimilar(double)
	 */
	public List<String> getStatisticallySimilar(String name, double significanceLevel) {
		List<String> result = new ArrayList<>();
		
		for (Pair<String, String> pair : getStatisticallySimilar(significanceLevel)) {
			if (pair.getLeft().equalsIgnoreCase(name)) {
				result.add(pair.getRight());
			} else if (pair.getRight().equalsIgnoreCase(name)) {
				result.add(pair.getLeft());
			}
		}
		
		return result;
	}
	
	/**
	 * Computes and returns all pairs that are statistically similar.
	 * <p>
	 * First applies the non-parametric Kruskal-Wallis test to determine if the medians are the same between {@code N}
	 * groups.  If differences are detected, then the Mann-Whitney U test is applied to all {@code N*(N-1)/2} pairs.
	 * Starting with the single test against all groups results in less overall error than using just pairwise tests.
	 * 
	 * @param significanceLevel the level of significance
	 * @return the statistically similar pairs
	 */
	public List<Pair<String, String>> getStatisticallySimilar(double significanceLevel) {
		List<Pair<String, String>> result = new ArrayList<>();
		List<String> groupNames = List.copyOf(data.keySet());
		KruskalWallisTest kwTest = new KruskalWallisTest(data.size());

		for (int i = 0; i < groupNames.size(); i++) {
			kwTest.addAll(data.get(groupNames.get(i)).getValues(), i);
		}
	
		if (!kwTest.test(significanceLevel)) {
			// No differences, all groups are similar
			for (int i = 0; i < groupNames.size() - 1; i++) {
				for (int j = i + 1; j < groupNames.size(); j++) {
					result.add(Pair.of(groupNames.get(i), groupNames.get(j)));
				}
			}
		} else {
			// Difference detected, test each pair
			for (int i = 0; i < groupNames.size() - 1; i++) {
				for (int j = i + 1; j < groupNames.size(); j++) {
					MannWhitneyUTest mwTest = new MannWhitneyUTest();
					mwTest.addAll(data.get(groupNames.get(i)).getValues(), 0);
					mwTest.addAll(data.get(groupNames.get(j)).getValues(), 1);

					if (!mwTest.test(significanceLevel)) {
						result.add(Pair.of(groupNames.get(i), groupNames.get(j)));
					}
				}
			}
		}
		
		return result;
	}
	
	@Override
	public TabularData<String> asTabularData() {
		TabularData<String> table = new TabularData<>(data.keySet());
		
		table.addColumn(new Column<>("Name", x -> x));
		table.addColumn(new Column<>("Min", this::getMin));
		table.addColumn(new Column<>("Median", this::getMedian));
		table.addColumn(new Column<>("Max", this::getMax));
		table.addColumn(new Column<>("IQR (+/-)", this::getIQR));
		table.addColumn(new Column<>("Count", this::getN));
		
		if (data.size() > 1) {
			table.addColumn(new Column<>("Statistically Similar (a=0.05)",
					x -> String.join(", ", getStatisticallySimilar(x, 0.05))));
		}
		
		return table;
	}

}
