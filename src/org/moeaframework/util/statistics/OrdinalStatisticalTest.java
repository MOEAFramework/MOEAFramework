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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Abstract class for implementing ordinal (rank-based) statistical tests.
 * Methods are provided for storing {@link RankedObservation} objects and
 * updating their ranks.
 */
public abstract class OrdinalStatisticalTest implements StatisticalTest {

	/**
	 * Compares two {@code RankedObservation} objects based on their value.
	 */
	private static class ObservationComparator implements
			Comparator<RankedObservation>, Serializable {

		private static final long serialVersionUID = 284381611483212771L;

		@Override
		public int compare(RankedObservation o1, RankedObservation o2) {
			if (o1.getValue() < o2.getValue()) {
				return -1;
			} else if (o1.getValue() > o2.getValue()) {
				return 1;
			} else {
				return 0;
			}
		}

	}

	/**
	 * The number of groups being tested.
	 */
	protected final int numberOfGroups;

	/**
	 * The comparator used for ordering observations.
	 */
	protected final Comparator<RankedObservation> comparator;

	/**
	 * Collection of all ranked observations added to this test.
	 */
	protected final List<RankedObservation> data;

	/**
	 * Constructs a new ordinal (rank-based) statistical test for the specified
	 * number of groups.
	 * 
	 * @param numberOfGroups the number of groups being tested
	 */
	public OrdinalStatisticalTest(int numberOfGroups) {
		this(numberOfGroups, new ObservationComparator());
	}

	/**
	 * Constructs a new ordinal (rank-based) statistical test for the specified
	 * number of groups and the comparator for ordering observations.
	 * 
	 * @param numberOfGroups the number of groups being tested
	 * @param comparator the comparator for ordering observations
	 */
	public OrdinalStatisticalTest(int numberOfGroups,
			Comparator<RankedObservation> comparator) {
		super();
		this.numberOfGroups = numberOfGroups;
		this.comparator = comparator;

		data = new ArrayList<RankedObservation>();
	}

	/**
	 * Adds a new observation with the specified value and group.
	 * 
	 * @param value the value of the new observation
	 * @param group the group to which the new observation belongs
	 */
	protected void add(double value, int group) {
		if ((group < 0) || (group >= numberOfGroups)) {
			throw new IllegalArgumentException();
		}

		data.add(new RankedObservation(value, group));
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
	 * Sorts the observations using the {@code comparator} and assigns ranks.
	 */
	protected void update() {
		Collections.sort(data, comparator);

		int i = 0;
		while (i < data.size()) {
			int j = i + 1;
			double rank = i + 1;

			while ((j < data.size())
					&& (data.get(i).getValue() == data.get(j).getValue())) {
				rank += j + 1;
				j++;
			}

			rank /= j - i;

			for (int k = i; k < j; k++) {
				data.get(k).setRank(rank);
			}

			i = j;
		}
	}

	/**
	 * Returns the number of observations used in this test.
	 * 
	 * @return the number of observations used in this test
	 */
	public int size() {
		return data.size();
	}

	/**
	 * Returns the number of groups being tested.
	 * 
	 * @return the number of groups being tested
	 */
	public int getNumberOfGroups() {
		return numberOfGroups;
	}

	/**
	 * Returns the comparator used by this test to order observations.
	 * 
	 * @return the comparator used by this test to order observations
	 */
	public Comparator<RankedObservation> getComparator() {
		return comparator;
	}

}
