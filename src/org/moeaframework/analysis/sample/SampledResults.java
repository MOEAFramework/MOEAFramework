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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.moeaframework.analysis.parameter.Parameter;
import org.moeaframework.analysis.parameter.ParameterSet;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.util.format.Column;
import org.moeaframework.util.format.Formattable;
import org.moeaframework.util.format.TabularData;

public class SampledResults<T> implements Formattable<Entry<Sample, T>> {

	private final ParameterSet<?> parameters;
	
	private final Map<Sample, T> results;
	
	public SampledResults() {
		this(null);
	}

	public SampledResults(ParameterSet<?> parameters) {
		super();
		this.parameters = parameters;
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
	
	public <L, R> List<List<T>> matrix(Parameter<L> leftParameter, List<L> leftValues, Parameter<R> rightParameter, List<R> rightValues) {
		List<List<T>> matrix = new ArrayList<>();
		
		for (L leftValue : leftValues) {
			List<T> row = new ArrayList<>();
					
			right: for (R rightValue : rightValues) {
				for (Sample sample : results.keySet()) {
					if (leftParameter.getValue(sample).equals(leftValue) && rightParameter.getValue(sample).equals(rightValue)) {
						row.add(results.get(sample));
						continue right;
					}
				}
				
				throw new FrameworkException("No sample found with " + leftParameter.getName() + "=" + leftValue +
						" and " + rightParameter.getName() + "=" + rightValue);
			}
			
			matrix.add(row);
		}
		
		return matrix;
	}

	@Override
	public TabularData<Entry<Sample, T>> asTabularData() {
		TabularData<Entry<Sample, T>> table = new TabularData<Entry<Sample, T>>(results.entrySet());
		
		if (parameters != null) {
			for (Parameter<?> parameter : parameters) {
				table.addColumn(new Column<Entry<Sample, T>, Object>(parameter.getName(),
						x -> parameter.getValue(x.getKey())));
			}
		} else if (!results.isEmpty()) {
			Sample sample = results.keySet().iterator().next();
			
			for (String key : sample.keySet()) {
				table.addColumn(new Column<Entry<Sample, T>, String>(key, x -> x.getKey().getString(key)));
			}
		}
		
		table.addColumn(new Column<Entry<Sample, T>, T>("Result", x -> x.getValue()));
		
		return table;
	}

}