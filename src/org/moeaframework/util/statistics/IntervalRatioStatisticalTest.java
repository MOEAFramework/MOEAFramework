/* Copyright 2009-2018 David Hadka
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
package org.moeaframework.util.statistics;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract class for interval ratio statistical tests.
 */
public abstract class IntervalRatioStatisticalTest implements StatisticalTest {

	/**
	 * The number of groups being tested.
	 */
	protected final int numberOfGroups;

	/**
	 * Collection of all observations added to this test.
	 */
	protected final List<Observation> data;

	/**
	 * Constructs an interval ratio statistical test with the specified number
	 * of groups.
	 * 
	 * @param numberOfGroups the number of groups being tested
	 */
	public IntervalRatioStatisticalTest(int numberOfGroups) {
		super();
		this.numberOfGroups = numberOfGroups;

		data = new ArrayList<Observation>();
	}

	/**
	 * Adds a new observation with the specified value and group.
	 * 
	 * @param value the value of the new observation
	 * @param group the group to which the new observation belongs
	 */
	protected void add(double value, int group) {
		if ((group < 0) || (group >= numberOfGroups)) {
			throw new IllegalArgumentException("invalid group");
		}

		data.add(new Observation(value, group));
	}
	
	/**
	 * Adds several new observations to the specified group.
	 * 
	 * @param values the values of the new observations
	 * @param group the group to which the new observations belong
	 */
	protected void addAll(double[] values, int group) {
		for (double value : values) {
			add(value, group);
		}
	}

	/**
	 * Organizes the observations by their group.
	 * 
	 * @return a list containing the vectorized values observed for each group
	 */
	protected List<double[]> categorize() {
		int[] n = new int[numberOfGroups];
		for (Observation observation : data) {
			n[observation.getGroup()]++;
		}

		List<double[]> groupedData = new ArrayList<double[]>();
		for (int i = 0; i < numberOfGroups; i++) {
			groupedData.add(new double[n[i]]);
		}

		for (Observation observation : data) {
			int group = observation.getGroup();
			n[group]--;
			groupedData.get(group)[n[group]] = observation.getValue();
		}

		return groupedData;
	}

	/**
	 * Returns the number of groups being tested.
	 * 
	 * @return the number of groups being tested
	 */
	public int getNumberOfGroups() {
		return numberOfGroups;
	}

}
