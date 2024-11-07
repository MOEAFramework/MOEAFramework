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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.moeaframework.analysis.parameter.Parameter;
import org.moeaframework.analysis.parameter.ParameterSet;
import org.moeaframework.util.format.Column;
import org.moeaframework.util.format.Formattable;
import org.moeaframework.util.format.TabularData;
import org.moeaframework.util.io.LineReader;
import org.moeaframework.util.io.Tokenizer;

/**
 * A collection of samples, typically associated with a parameter set that generated the samples.
 */
public class Samples implements Iterable<Sample>, Formattable<Sample> {

	private final ParameterSet parameterSet;

	private final List<Sample> samples;
	
	public Samples() {
		this(null);
	}

	public Samples(ParameterSet parameterSet) {
		super();
		this.parameterSet = parameterSet;
		this.samples = Collections.synchronizedList(new ArrayList<>());
	}

	public Samples(ParameterSet parameterSet, Collection<Sample> samples) {
		this(parameterSet);
		addAll(samples);
	}

	public Samples(ParameterSet parameterSet, Iterable<Sample> samples) {
		this(parameterSet);
		addAll(samples);
	}
	
	public ParameterSet getParameterSet() {
		return parameterSet;
	}

	public int size() {
		return samples.size();
	}

	public boolean isEmpty() {
		return samples.isEmpty();
	}

	void add(Sample sample) {
		this.samples.add(sample);
	}

	void addAll(Collection<Sample> samples) {
		this.samples.addAll(samples);
	}

	void addAll(Iterable<Sample> samples) {
		for (Sample sample : samples) {
			add(sample);
		}
	}
	
	public Sample get(int index) {
		return samples.get(index);
	}
	
	public <T> List<T> distinctValues(Parameter<T> parameter) {
		return samples.stream().map(x -> parameter.parse(x.getString(parameter.getName()))).distinct().toList();
	}

	@Override
	public Iterator<Sample> iterator() {
		return Collections.unmodifiableList(samples).iterator();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37)
				.append(samples)
				.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj == this) {
			return true;
		}

		if (obj.getClass() != getClass()) {
			return false;
		}

		Samples rhs = (Samples)obj;
		return new EqualsBuilder()
				.append(samples, rhs.samples)
				.isEquals();
	}
	
	@Override
	public TabularData<Sample> asTabularData() {
		TabularData<Sample> table = new TabularData<Sample>(samples);
		
		for (Parameter<?> parameter : parameterSet) {
			table.addColumn(new Column<Sample, Object>(parameter.getName(), x -> parameter.readValue(x)));
		}
				
		return table;
	}

	public static Samples load(File file, ParameterSet parameterSet) throws FileNotFoundException, IOException {
		try (FileReader reader = new FileReader(file)) {
			return load(reader, parameterSet);
		}
	}
	
	public static Samples load(Reader reader, ParameterSet parameterSet) throws IOException {
		Tokenizer tokenizer = new Tokenizer();
		Samples samples = new Samples(parameterSet);
		
		try (LineReader lineReader = LineReader.wrap(reader).skipComments().skipBlanks()) {
			for (String line : lineReader) {
				String[] tokens = tokenizer.decodeToArray(line);
				
				if (tokens.length != parameterSet.size()) {
					throw new IOException("invalid line: " + line);
				}
				
				Sample sample = new Sample();
				
				for (int i = 0; i < parameterSet.size(); i++) {
					Parameter<?> parameter = parameterSet.get(i);
					parameter.parse(tokens[i]);
					sample.setString(parameter.getName(), tokens[i]);
				}
				
				samples.add(sample);
			}
		}
		
		return samples;
	}

	public void save(File file) throws IOException {
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
			save(writer);
		}
	}
	
	public void save(Writer writer) throws IOException {
		Tokenizer tokenizer = new Tokenizer();
		
		for (Sample sample : samples) {
			for (int i = 0; i < parameterSet.size(); i++) {
				if (i > 0) {
					writer.write(tokenizer.getDelimiter());
				}
				
				writer.write(tokenizer.escape(sample.getString(parameterSet.get(i).getName())));
			}
			
			writer.write(System.lineSeparator());
		}
	}

}