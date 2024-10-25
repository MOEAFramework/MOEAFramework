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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.moeaframework.analysis.parameter.Parameter;
import org.moeaframework.analysis.parameter.ParameterSet;
import org.moeaframework.util.format.Column;
import org.moeaframework.util.format.Formattable;
import org.moeaframework.util.format.TabularData;

public class SampledResults<T> implements Formattable<Entry<Sample, T>> {

	private final ParameterSet parameterSet;
	
	private final Map<Sample, T> results;
	
	public SampledResults(Samples samples) {
		this(samples.getParameterSet());
	}

	public SampledResults(ParameterSet parameterSet) {
		super();
		this.parameterSet = parameterSet;
		this.results = Collections.synchronizedMap(new LinkedHashMap<>());
	}

	public int size() {
		return results.size();
	}
	
	public void set(Sample sample, T result) {
		results.put(sample, result);
	}

	public T get(Sample sample) {
		return results.get(sample);
	}

	@Override
	public TabularData<Entry<Sample, T>> asTabularData() {
		TabularData<Entry<Sample, T>> table = new TabularData<Entry<Sample, T>>(results.entrySet());
		
		for (Parameter<?> parameter : parameterSet) {
			table.addColumn(new Column<Entry<Sample, T>, Object>(parameter.getName(),
					x -> parameter.readValue(x.getKey())));
		}
		
		table.addColumn(new Column<Entry<Sample, T>, T>("Result", x -> x.getValue()));
		
		return table;
	}

}