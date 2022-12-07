/* Copyright 2009-2022 David Hadka
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
package org.moeaframework.analysis.collector;

import java.io.Serializable;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.moeaframework.util.format.Column;
import org.moeaframework.util.format.Formattable;
import org.moeaframework.util.format.TabularData;

/**
 * The observations collected over the course of a single algorithm run.
 */
public class Observations implements Serializable, Iterable<Observation>, Formattable<Observation> {

	private static final long serialVersionUID = -5488946450924958321L;

	/**
	 * The internal storage of observations.
	 */
	private final SortedSet<Observation> observations;

	/**
	 * Constructs an empty observations object.
	 */
	public Observations() {
		observations = new TreeSet<Observation>();
	}

	/**
	 * Adds a new observation to this collection.
	 * 
	 * @param observation the observation
	 */
	public void add(Observation observation) {
		observations.add(observation);
	}
	
	/**
	 * Returns the number of observations recorded.
	 * 
	 * @return the number of observations
	 */
	public int size() {
		return observations.size();
	}
	
	/**
	 * Returns the keys - the name of individual observations - that have been recorded.
	 * This assumes that each recorded observation contains identical keys.
	 * 
	 * @return the keys
	 */
	public Set<String> keys() {
		if (observations.isEmpty()) {
			return Collections.emptySet();
		} else {
			return observations.first().keys();
		}
	}
	
	/**
	 * Returns the first observation.
	 * 
	 * @return the first observation
	 */
	public Observation first() {
		return observations.first();
	}
	
	/**
	 * Returns the observation at the specified NFE.  If there is no exact match,
	 * it returns the next largest NFE.  If at the end of the list, returns {@code null}.
	 * 
	 * @param NFE the NFE to locate
	 * @return the matching observation
	 */
	public Observation at(int NFE) {
		// TODO: given the data is sorted, there should be a faster way to do this (binary search)
		for (Observation observation : observations) {
			if (observation.getNFE() >= NFE) {
				return observation;
			}
		}
		
		return null;
	}
	
	/**
	 * Returns the last observation.
	 * 
	 * @return the last observation
	 */
	public Observation last() {
		return observations.last();
	}

	@Override
	public Iterator<Observation> iterator() {
		return observations.iterator();
	}

	@Override
	public TabularData<Observation> asTabularData() {
		TabularData<Observation> data = new TabularData<Observation>(this);
		
		if (!observations.isEmpty()) {
			Observation observation = observations.first();
			
			data.addColumn(new Column<Observation, Integer>("NFE", o -> o.getNFE()));
			
			for (final String key : observation.keys()) {
				Object value = observation.get(key);
				
				// exclude non-numeric values
				if (value instanceof Number) {
					data.addColumn(new Column<Observation, Number>(key, o -> (Number)o.get(key)));
				}
			}
		}
		
		return data;
	}

}
