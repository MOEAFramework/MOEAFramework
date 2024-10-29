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
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Pair;
import org.moeaframework.analysis.parameter.Parameter;
import org.moeaframework.analysis.parameter.ParameterSet;
import org.moeaframework.analysis.stream.Partition;
import org.moeaframework.util.format.Column;
import org.moeaframework.util.format.TabularData;

public class SampledResults<T> implements Partition<Sample, T> {

	private final ParameterSet parameterSet;
	
	private final List<Pair<Sample, T>> results;
		
	public SampledResults(Samples samples) {
		this(samples.getParameterSet());
	}

	public SampledResults(ParameterSet parameterSet) {
		super();
		this.parameterSet = parameterSet;
		this.results = new ArrayList<>();
	}
	
	public void add(Sample sample, T result) {
		results.add(Pair.of(sample, result));
	}
	
	@Override
	public int size() {
		return results.size();
	}
	
	@Override
	public Stream<Pair<Sample, T>> stream() {
		return results.stream();
	}

	@Override
	public TabularData<Pair<Sample, T>> asTabularData() {
		TabularData<Pair<Sample, T>> table = new TabularData<Pair<Sample, T>>(results);
		
		for (Parameter<?> parameter : parameterSet) {
			table.addColumn(new Column<Pair<Sample, T>, Object>(parameter.getName(),
					x -> parameter.readValue(x.getKey())));
		}
		
		table.addColumn(new Column<Pair<Sample, T>, T>("Result", x -> x.getValue()));
		
		return table;
	}

}