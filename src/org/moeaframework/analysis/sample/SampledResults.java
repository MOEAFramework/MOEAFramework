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

import org.apache.commons.lang3.tuple.Pair;
import org.moeaframework.analysis.parameter.EnumeratedParameter;
import org.moeaframework.analysis.parameter.Parameter;
import org.moeaframework.analysis.parameter.ParameterSet;
import org.moeaframework.analysis.stream.ImmutablePartition;
import org.moeaframework.analysis.stream.MutablePartition;
import org.moeaframework.analysis.stream.Partition;
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
	
	public <V> Partition<V, T> project(EnumeratedParameter<V> parameter) {
		return new ImmutablePartition<V, T>(parameter.values().stream()
			.map(x -> Pair.of(x, filter(y -> parameter.readValue(y).equals(x)).any().getValue())));
	}
	
	public <L, R> Partition<Pair<L, R>, T> project(EnumeratedParameter<L> left, EnumeratedParameter<R> right) {
		return new ImmutablePartition<Pair<L, R>, T>(left.values().stream().flatMap(l ->
			right.values().stream().map(r ->
				Pair.of(Pair.of(l, r), filter(x ->
					left.readValue(x).equals(l) && right.readValue(x).equals(r)).any().getValue()))));
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