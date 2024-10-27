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
package org.moeaframework.analysis.sample;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.moeaframework.analysis.parameter.EnumeratedParameter;
import org.moeaframework.analysis.parameter.Parameter;
import org.moeaframework.analysis.parameter.ParameterSet;
import org.moeaframework.analysis.stream.Groupings;
import org.moeaframework.analysis.stream.Groups;
import org.moeaframework.analysis.stream.MutablePartition;
import org.moeaframework.util.format.Column;
import org.moeaframework.util.format.TabularData;

public class SampledResults<T> extends MutablePartition<Sample, T> {

	private final ParameterSet parameterSet;
		
	public SampledResults(Samples samples) {
		this(samples.getParameterSet());
	}

	public SampledResults(ParameterSet parameterSet) {
		super();
		this.parameterSet = parameterSet;
	}
	
	public <V> Groups<V, Sample, T> groupBy(Parameter<V> parameter) {
		return groupBy(Groupings.exactValue(parameter));
	}
	
	public <V> Pair<List<V>, List<T>> project(EnumeratedParameter<V> parameter) {
		List<V> keys = parameter.values();
		List<T> values = new ArrayList<>();
				
		for (V key : keys) {
			values.add(filter(x -> parameter.readValue(x).equals(key)).any().getValue());
		}

		return Pair.of(keys, values);
	}
	
	public <L, R> Triple<List<L>, List<R>, List<List<T>>> project(EnumeratedParameter<L> left, EnumeratedParameter<R> right) {
		List<L> leftKeys = left.values();
		List<R> rightKeys = right.values();
		List<List<T>> values = new ArrayList<>();
				
		for (L leftKey : leftKeys) {
			List<T> row = new ArrayList<>();
			
			for (R rightKey : rightKeys) {
				row.add(filter(x ->
					left.readValue(x).equals(leftKey) && right.readValue(x).equals(rightKey)).any().getValue());
			}
			
			values.add(row);
		}

		return Triple.of(leftKeys, rightKeys, values);
	}

	@Override
	public TabularData<Pair<Sample, T>> asTabularData() {
		TabularData<Pair<Sample, T>> table = new TabularData<Pair<Sample, T>>(content);
		
		for (Parameter<?> parameter : parameterSet) {
			table.addColumn(new Column<Pair<Sample, T>, Object>(parameter.getName(),
					x -> parameter.readValue(x.getKey())));
		}
		
		table.addColumn(new Column<Pair<Sample, T>, T>("Result", x -> x.getValue()));
		
		return table;
	}

}